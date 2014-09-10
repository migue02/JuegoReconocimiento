#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "opencv2/nonfree/features2d.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/calib3d/calib3d.hpp"
#include <android/log.h>
#include <ctime>

using namespace std;
using namespace cv;

extern "C" {

vector<Mat> vectorDescriptores;
vector<vector<KeyPoint> > vectorKeyPoints;
vector<int> listaCols;
vector<int> listaRows;
int numMatches = 10;

double hessianThreshold = 800.0;
int nOctaves = 4;
int nOctaveLayers = 2;
bool extended = true;
bool upright = false;

double nPorcentaje = 0.5;

int nMatcher = 1;

void Mat_to_vector_KeyPoint(Mat& mat, vector<KeyPoint>& v_kp) {
	v_kp.clear();
	if (mat.type() == CV_32FC(7) && mat.cols == 1)
		for (int i = 0; i < mat.rows; i++) {
			Vec<float, 7> v = mat.at < Vec<float, 7> > (i, 0);
			KeyPoint kp(v[0], v[1], v[2], v[3], v[4], (int) v[5], (int) v[6]);
			v_kp.push_back(kp);
		}
	return;
}
void vector_KeyPoint_to_Mat(vector<KeyPoint>& v_kp, Mat& mat) {
	int count = (int) v_kp.size();
	mat.create(count, 1, CV_32FC(7));
	for (int i = 0; i < count; i++) {
		KeyPoint kp = v_kp[i];
		mat.at < Vec<float, 7> > (i, 0) = Vec<float, 7>(kp.pt.x, kp.pt.y,
				kp.size, kp.angle, kp.response, (float) kp.octave,
				(float) kp.class_id);
	}
}
void freeObjects() {
	for (int i = 0; i < listaCols.size(); i++) {
		vectorDescriptores.at(i).release();
		vectorKeyPoints.at(i).clear();
	}
	vectorDescriptores.clear();
	vectorKeyPoints.clear();
	listaCols.clear();
	listaRows.clear();
}

JNIEXPORT jint JNICALL Java_es_ugr_reconocimiento_Juego_RellenarObjetos(
		JNIEnv * env, jobject, jlongArray descriptors, jlongArray keyPoints,
		jintArray colsArray, jintArray rowsArray) {
	freeObjects();
	jsize length = env->GetArrayLength(descriptors);
	if (length > 0) {
		// ----------------------------
		// Inicialización de los arrays
		// ----------------------------
		jlong *descriptorsData = env->GetLongArrayElements(descriptors, 0);
		jlong *keyPointsData = env->GetLongArrayElements(keyPoints, 0);
		jint *colsData = env->GetIntArrayElements(colsArray, 0);
		jint *rowsData = env->GetIntArrayElements(rowsArray, 0);

		for (int k = 0; k < length; k++) {
			// -------------------------------------------
			// Relleno del vector de vectores de KeyPoints
			// -------------------------------------------
			Mat & tempMatKeyPoint = *(Mat*) keyPointsData[k];
			vector<KeyPoint> tempVectorKeyPoint;
			Mat_to_vector_KeyPoint(tempMatKeyPoint, tempVectorKeyPoint);
			vectorKeyPoints.push_back(tempVectorKeyPoint);

			// ----------------------------------------------
			// Relleno del vector de matrices de descriptores
			// ----------------------------------------------
			Mat & tempDesctiptor = *(Mat*) descriptorsData[k];
			vectorDescriptores.push_back(tempDesctiptor);

			// ------------------------------------------------------------------------
			// Relleno de la anchura y largura del objeto (tamaño de la imagen inicial)
			// ------------------------------------------------------------------------
			listaCols.push_back(colsData[k]);
			listaRows.push_back(rowsData[k]);

		}

		// --------------------------------------------
		// Liberación de los arrays antes inicializados
		// --------------------------------------------
		env->ReleaseLongArrayElements(descriptors, descriptorsData, 0);
		env->ReleaseLongArrayElements(keyPoints, keyPointsData, 0);
		env->ReleaseIntArrayElements(colsArray, colsData, 0);
		env->ReleaseIntArrayElements(colsArray, rowsData, 0);
	}
	return length;
}

JNIEXPORT float JNICALL Java_es_ugr_reconocimiento_Juego_FindFeatures(
		JNIEnv* env, jobject, jlong addrGray, jlong addrRgba,
		jlong addrDescriptores, jlong addrKeyPoints) {

	clock_t t;
	t = clock();
	// -----------------------
	// Crear matrices y vector
	// -----------------------
	Mat& mGr = *(Mat*) addrGray;
	Mat& mRgb = *(Mat*) addrRgba;
	Mat& descriptores = *(Mat*) addrDescriptores;
	Mat& key = *(Mat*) addrKeyPoints;
	vector<KeyPoint> keyPoints;

	// -------------------
	// Inicializacion SURF
	// -------------------
	SurfFeatureDetector detector_Surf(hessianThreshold, nOctaves, nOctaveLayers,
			extended, upright);
	SurfDescriptorExtractor extractor_Surf;

	// -----------------------------------------------------------------
	// Deteccion de keypoints y extraccion de caracteristicas del objeto
	// -----------------------------------------------------------------
	detector_Surf.detect(mGr, keyPoints);
	if (keyPoints.size() > 0) {
		vector_KeyPoint_to_Mat(keyPoints, key);
		extractor_Surf.compute(mGr, keyPoints, descriptores);
		putText(mRgb, "Patron adquirido", Point2f(100, 100), FONT_HERSHEY_PLAIN,
				2, Scalar(0, 0, 255, 150), 2);
	}

	t = clock() - t;
	float elapsedTime = ((float) t / CLOCKS_PER_SEC);
	// ------------------------------------
	// Se pintan los keyPoints en la imagen
	// ------------------------------------
	for (unsigned int i = 0; i < keyPoints.size(); i++) {
		const KeyPoint& kp = keyPoints[i];
		circle(mRgb, Point(kp.pt.x, kp.pt.y), 10, Scalar(255, 0, 0, 255));
	}

	return elapsedTime;

}

JNIEXPORT void JNICALL Java_es_ugr_reconocimiento_Juego_CambiarValoresAlgoritmo(
		JNIEnv* env, jobject, jint pMatcher, jdouble pdPorcentaje, jdouble pdhessianThreshold) {
	nMatcher = pMatcher;
	nPorcentaje = pdPorcentaje;
	hessianThreshold = pdhessianThreshold;
}

/**************/
/***MATCHING***/
/**************/
float radio = 0.65f; // max radio between 1st and 2nd NN
bool refineF = true; // if true will refine the F matrix
double distancia = 1.0; // min distance to epipolar
double confidence = 0.98; // confidence level (probability)

// Clear matches for which NN radio is > than threshold
// return the number of removed points
// (corresponding entries being cleared,
// i.e. size will be 0)
int radioTest(vector<vector<DMatch> > &matches) {
	int removed = 0;
	// for all matches
	for (vector<vector<DMatch> >::iterator matchIterator = matches.begin();
			matchIterator != matches.end(); ++matchIterator) {
		// if 2 NN has been identified
		if (matchIterator->size() > 1) {
			// check distance radio
			if ((*matchIterator)[0].distance / (*matchIterator)[1].distance
					> radio) {
				matchIterator->clear(); // remove match
				removed++;
			}
		} else { // does not have 2 neighbours
			matchIterator->clear(); // remove match
			removed++;
		}
	}
	return removed;
}

// Insert symmetrical matches in symMatches vector
void symmetryTest(const vector<vector<DMatch> >& matches1,
		const vector<vector<DMatch> >& matches2, vector<DMatch>& symMatches) {
	// for all matches image 1 -> image 2
	for (vector<vector<DMatch> >::const_iterator matchIterator1 =
			matches1.begin(); matchIterator1 != matches1.end();
			++matchIterator1) {
		// ignore deleted matches
		if (matchIterator1->size() < 2)
			continue;
		// for all matches image 2 -> image 1
		for (vector<vector<DMatch> >::const_iterator matchIterator2 =
				matches2.begin(); matchIterator2 != matches2.end();
				++matchIterator2) {
			// ignore deleted matches
			if (matchIterator2->size() < 2)
				continue;
			// Match symmetry test
			if ((*matchIterator1)[0].queryIdx == (*matchIterator2)[0].trainIdx
					&& (*matchIterator2)[0].queryIdx
							== (*matchIterator1)[0].trainIdx) {
				// add symmetrical match
				symMatches.push_back(
						DMatch((*matchIterator1)[0].queryIdx,
								(*matchIterator1)[0].trainIdx,
								(*matchIterator1)[0].distance));
				break; // next match in image 1 -> image 2
			}
		}
	}
}

// Identify good matches using RANSAC
// Return fundemental matrix
Mat ransacTest(const vector<DMatch>& matches,
		const vector<KeyPoint>& keypoints1, const vector<KeyPoint>& keypoints2,
		vector<DMatch>& outMatches) {
	// Convert keypoints into Point2f
	vector<Point2f> points1, points2;
	for (vector<DMatch>::const_iterator it = matches.begin();
			it != matches.end(); ++it) {
		// Get the position of left keypoints
		float x = keypoints1[it->queryIdx].pt.x;
		float y = keypoints1[it->queryIdx].pt.y;
		points1.push_back(Point2f(x, y));
		// Get the position of right keypoints
		x = keypoints2[it->trainIdx].pt.x;
		y = keypoints2[it->trainIdx].pt.y;
		points2.push_back(Point2f(x, y));
	}
	// Compute F matrix using RANSAC
	vector<uchar> inliers(points1.size(), 0);
	__android_log_write(ANDROID_LOG_ERROR, "match", "fundemental in");
	Mat fundemental = findFundamentalMat(Mat(points1), Mat(points2), // matching points
	inliers, // match status (inlier or outlier)
			CV_FM_RANSAC, // RANSAC method
			distancia, // distance to epipolar line
			confidence); // confidence probability
	__android_log_write(ANDROID_LOG_ERROR, "match", "fundemental out");
	// extract the surviving (inliers) matches
	vector<uchar>::const_iterator itIn = inliers.begin();
	vector<DMatch>::const_iterator itM = matches.begin();
	// for all matches
	for (; itIn != inliers.end(); ++itIn, ++itM) {
		if (*itIn) { // it is a valid match
			outMatches.push_back(*itM);
		}
	}

	if (refineF) {
		// The F matrix will be recomputed with
		// all accepted matches
		// Convert keypoints into Point2f
		// for final F computation
		points1.clear();
		points2.clear();
		for (vector<DMatch>::const_iterator it = outMatches.begin();
				it != outMatches.end(); ++it) {
			// Get the position of left keypoints
			float x = keypoints1[it->queryIdx].pt.x;
			float y = keypoints1[it->queryIdx].pt.y;
			points1.push_back(Point2f(x, y));
			// Get the position of right keypoints
			x = keypoints2[it->trainIdx].pt.x;
			y = keypoints2[it->trainIdx].pt.y;
			points2.push_back(Point2f(x, y));
		}
		// Compute 8-point F from all accepted matches
		fundemental = findFundamentalMat(Mat(points1), Mat(points2), // matches
		CV_FM_8POINT); // 8-point method
	}
	return fundemental;

}

vector<DMatch> FlannMatch(Mat descriptores_obj, Mat descriptores_esc,
		int &nMatches) {
	FlannBasedMatcher matcher;
	vector<DMatch> good_matches;
	vector<vector<DMatch> > matches;

	try {
		matcher.knnMatch(descriptores_obj, descriptores_esc, matches, 2);

		nMatches = matches.size();

		// ----------------------------------------------------------------------
		// Draw only "good" matches (i.e. whose distance is less than 2*min_dist,
		// or a small arbitary value ( 0.02 ) in the event that min_dist is very
		// small)
		// PS.- radiusMatch can also be used here.
		// ----------------------------------------------------------------------

		for (int i = 0;
				i < min(descriptores_obj.rows - 1, (int) matches.size()); i++)//THIS LOOP IS SENSITIVE TO SEGFAULTS
				{
			if ((matches[i][0].distance < 0.6 * (matches[i][1].distance))
					&& ((int) matches[i].size() <= 2
							&& (int) matches[i].size() > 0)) {
				good_matches.push_back(matches[i][0]);
			}
		}
	} catch (Exception e) {
		__android_log_write(ANDROID_LOG_ERROR, "match", "NO");
	}
	return good_matches;
}

vector<DMatch> BruteForceMatch(Mat descriptores_obj, Mat descriptores_esc,
		int &nMatches) {
	BFMatcher matcher;
	vector<vector<DMatch> > matches;
	matcher.knnMatch(descriptores_obj, descriptores_esc, matches, 2); // Find two nearest matches
	nMatches = matches.size();
	vector<DMatch> good_matches;
	for (int i = 0; i < matches.size(); ++i) {
		const float radio = 0.8; // As in Lowe's paper; can be tuned
		if (matches[i][0].distance < radio * matches[i][1].distance) {
			good_matches.push_back(matches[i][0]);
		}
	}
	return good_matches;
}

/**
 * @param mrGr imagen gris del frame actual (escenario)
 * @param mRgb imagen color del frame actual (escenario)
 * @param keyPoints_esc keyPoints obtenidos al procesar el escenario
 * @param descriptores_esc Descriptores obtenidos al procesar el escenario
 * @param nObjeto índice del objeto que se está buscando en el escenario
 * @return Devuelve si el objeto nObjeto se han encontrado en el escenario
 */
int encuentraObjeto(Mat mrGr, Mat mRgb, vector<KeyPoint> keyPoints_esc,
		Mat descriptores_esc, int nObjeto) {
	// ---------------------------------------------------------------------------
	// Inicializar vector de KeyPoints y matriz de Descriptores del objeto nObjeto
	// ---------------------------------------------------------------------------
	Mat descriptores_obj = vectorDescriptores.at(nObjeto);
	vector<KeyPoint> keyPoints_obj = vectorKeyPoints.at(nObjeto);
	int cols = listaCols.at(nObjeto);
	int rows = listaRows.at(nObjeto);

	if (keyPoints_obj.size() == 0 || descriptores_obj.rows == 0)
		return -1;
	// ----------------------------------------------------------------------
	// Obtencion de los matches entre el objeto y el escenario mediante FLANN
	// ----------------------------------------------------------------------

	int nMatches = 0;
	int nGoodMatches = -1;
	try {

		vector<DMatch> good_matches;

		BFMatcher matcher1;
		vector<vector<DMatch> > matches1;
		vector<vector<DMatch> > matches2;
		int removed;
		vector<DMatch> symMatches;
		switch (nMatcher) {
		case 0:
			__android_log_write(ANDROID_LOG_ERROR, "match", "FLANN MATCHER");
			good_matches = FlannMatch(descriptores_obj, descriptores_esc,
					nMatches);
			break;
		case 1:
			//__android_log_write(ANDROID_LOG_ERROR, "match",
			//		"BRUTE FORCE MATCHER");
			good_matches = BruteForceMatch(descriptores_obj, descriptores_esc,
					nMatches);
			break;
		case 2:
			__android_log_write(ANDROID_LOG_ERROR, "match", "ROBUST MATCHER");
			// 2. Match the two image descriptors
			matcher1.knnMatch(descriptores_obj, descriptores_esc, matches1, 2); // Find two nearest matches
			matcher1.knnMatch(descriptores_esc, descriptores_obj, matches2, 2); // return 2 nearest neighbours
			// 3. Remove matches for which NN ratio is > than threshold
			// clean image 1 -> image 2 matches
			removed = 0;
			// for all matches
			for (vector<vector<DMatch> >::iterator matchIterator = matches1.begin();
					matchIterator != matches1.end(); ++matchIterator) {
				// if 2 NN has been identified
				if (matchIterator->size() > 1) {
					// check distance radio
					if ((*matchIterator)[0].distance / (*matchIterator)[1].distance
							> radio) {
						matchIterator->clear(); // remove match
						removed++;
					}
				} else { // does not have 2 neighbours
					matchIterator->clear(); // remove match
					removed++;
				}
			}
			// clean image 2 -> image 1 matches
			removed = 0;
			for (vector<vector<DMatch> >::iterator matchIterator = matches2.begin();
					matchIterator != matches2.end(); ++matchIterator) {
				if (matchIterator->size() > 1) {
					if ((*matchIterator)[0].distance / (*matchIterator)[1].distance
							> radio) {
						matchIterator->clear();
						removed++;
					}
				} else {
					matchIterator->clear();
					removed++;
				}
			}
			// 4. Remove non-symmetrical matches
			symmetryTest(matches1, matches2, symMatches);
			// 5. Validate matches using RANSAC
			mRgb = ransacTest(symMatches, keyPoints_obj, keyPoints_esc, good_matches);
			nGoodMatches = good_matches.size();
			char buffer[10];
			sprintf(buffer, "%i", nGoodMatches);
			__android_log_write(ANDROID_LOG_ERROR, "match", buffer);
			return nGoodMatches;
			break;
		default:
			break;
		}

		// ---------------------------------------------------------------------------------
		// Si los goodPoints coinciden con el 60% o más, entonces se ha encontrado el objeto
		// ---------------------------------------------------------------------------------
		float lfPorcentaje = nMatches * nPorcentaje;
		int lnPorcentaje = lfPorcentaje;
		if (nMatcher == 0)
			lnPorcentaje = 8;
		else{
			char au[150], ptn[100];
			nGoodMatches = good_matches.size();
			strcpy(au, "\nHay = ");
			sprintf(ptn, "%i", nGoodMatches);
			strcat(au, ptn);
			strcpy(ptn, " good matches, y el ");
			strcat(au, ptn);
			sprintf(ptn, "%f", nPorcentaje*100);
			strcat(au, ptn);
			strcpy(ptn, " de los matches del objeto es ");
			strcat(au, ptn);
			sprintf(ptn, "%i", lnPorcentaje);
			strcat(au, ptn);
			__android_log_write(ANDROID_LOG_ERROR, "match", au);
		}
		if (nGoodMatches >= lnPorcentaje) {

			vector<Point2f> obj;
			vector<Point2f> scene;

			for (int i = 0; i < nGoodMatches; i++) {
				//-- Get the keypoints from the good matches
				obj.push_back(keyPoints_obj[good_matches[i].queryIdx].pt);
				scene.push_back(keyPoints_esc[good_matches[i].trainIdx].pt);
			}

			// ----------------------------------------------------------------------------
			// Skip doing homography if the object and scene contains less than four points
			// (cant draw a rectangle if less than 4 points, hence your program will crash
			// here if you do not handle the exception)
			// ----------------------------------------------------------------------------
			if (obj.size() < 4 || scene.size() < 4)
				return -1;

			// -------------------------------------------------------------------------------------------------------
			// Encontrar la homografía, para poder dibujar un rectángulo que englobe al objeto nObjeto en el escenario
			// -------------------------------------------------------------------------------------------------------
			Mat H = findHomography(obj, scene, CV_RANSAC);

			vector<Point2f> obj_corners(4);
			obj_corners[0] = cvPoint(0, 0);
			obj_corners[1] = cvPoint(cols, 0);
			obj_corners[2] = cvPoint(cols, rows);
			obj_corners[3] = cvPoint(0, rows);
			vector<Point2f> scene_corners(4);

			perspectiveTransform(obj_corners, scene_corners, H);

			line(mRgb, scene_corners[0], scene_corners[1], Scalar(0, 255, 0),
					4);
			line(mRgb, scene_corners[1], scene_corners[2], Scalar(255, 0, 0),
					4);
			line(mRgb, scene_corners[2], scene_corners[3], Scalar(0, 0, 255),
					4);
			line(mRgb, scene_corners[3], scene_corners[0],
					Scalar(255, 255, 255), 4);

			for (unsigned int i = 0; i < scene.size(); i++) {
				const Point2f& kp = scene[i];
				circle(mRgb, Point(kp.x, kp.y), 10, Scalar(255, 0, 0, 255));
			}

			putText(mRgb, "Encontrado", Point2f(100, 100), FONT_HERSHEY_PLAIN,
					2, Scalar(0, 0, 255, 150), 2);

			return nGoodMatches;

		}
	} catch (Exception e) {
		__android_log_write(ANDROID_LOG_ERROR, "match", "ERROR_MATCH");
	}
	return -1;
}

vector<KeyPoint> keyPoints_esc;
Mat descriptores_esc;

void LiberaEscenario() {
	keyPoints_esc.clear();
	descriptores_esc.release();
}

bool InicializaEscenario(Mat mGr, Mat mRgb) {
	// -------------------
	// Inicializacion SURF
	// -------------------
	SurfFeatureDetector detector_Surf(hessianThreshold, nOctaves, nOctaveLayers,
			extended, upright);
	SurfDescriptorExtractor extractor_Surf;

	// --------------------------------------------------------------------
	// Deteccion de keypoints y extraccion de caracteristicas del escenario
	// --------------------------------------------------------------------

	detector_Surf.detect(mGr, keyPoints_esc);
	if (keyPoints_esc.size() == 0) {
		keyPoints_esc.clear();
		return false;
	}
	extractor_Surf.compute(mGr, keyPoints_esc, descriptores_esc);
	if (descriptores_esc.rows == 0) {
		keyPoints_esc.clear();
		descriptores_esc.release();
		return false;
	}
	return true;
}

JNIEXPORT jint JNICALL Java_es_ugr_reconocimiento_Juego_FindObjects(JNIEnv* env,
		jobject, jlong addrGray, jlong addrRgba) {

	jint nObjeto = -1;

	// ----------------------------
	// Inicializar variables a usar
	// ----------------------------
	Mat& mGr = *(Mat*) addrGray;
	Mat& mRgb = *(Mat*) addrRgba;

	int match, matchMaximo = -1;

	if (InicializaEscenario(mGr, mRgb)) {

		// -----------------------------------------------------------------------------------------------
		// Bucle que terminar si se encuentra el objeto en el escenario o si no hay más objetos que buscar
		// -----------------------------------------------------------------------------------------------
		__android_log_write(ANDROID_LOG_ERROR, "match", "Empezamos el match");
		for (int i = 0; i < listaCols.size(); i++) {
			match = encuentraObjeto(mGr, mRgb, keyPoints_esc, descriptores_esc,
					i);
			//char buffer[10];
			//sprintf(buffer, "%i", match);
			//__android_log_write(ANDROID_LOG_ERROR, "match", buffer);
			clock_t time_end;
			time_end = clock() + 0 * CLOCKS_PER_SEC / 1000;
			while (clock() < time_end) {
			}
			if (matchMaximo < match && match != -1) {
				matchMaximo = match;
				nObjeto = i;
			}
		}

		LiberaEscenario();

	}

	return nObjeto;
}

JNIEXPORT jint JNICALL Java_es_ugr_reconocimiento_Juego_LiberaObjetos() {
	freeObjects();
}

JNIEXPORT void JNICALL Java_es_ugr_reconocimiento_Juego_InicializaSurf(
		JNIEnv* env, jobject, jdouble phessian, jint pnOctaves,
		jint pnOctaveLayers, jboolean pExtended, jboolean pUpright) {
	if (phessian > 0)
		hessianThreshold = phessian;
	if (pnOctaves > 0)
		nOctaves = pnOctaves;
	if (pnOctaveLayers > 0)
		nOctaveLayers = pnOctaveLayers;
	extended = pExtended;
	upright = pUpright;
}

}
