/*
 * Libreria.h
 *
 *  Created on: 22/06/2014
 *      Author: Migue
 */

#ifndef LIBRERIA_H_
#define LIBRERIA_H_

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

using namespace std;
using namespace cv;

vector<KeyPoint> keyPoints_esc;
Mat descriptores_esc;

vector<Mat> vectorDescriptores;
vector<vector<KeyPoint> > vectorKeyPoints;
vector<int> listaCols;
vector<int> listaRows;
int numMatches=10;

double hessianThreshold=1300.0;
int nOctaves=4;
int nOctaveLayers=2;
bool extended=true;
bool upright=false;

int nMatches = 4;


void Mat_to_vector_KeyPoint(Mat& mat, vector<KeyPoint>& v_kp);
void vector_KeyPoint_to_Mat(vector<KeyPoint>& v_kp, Mat& mat);
void freeObjects();
bool InicializaEscenario(Mat mGr, Mat mRgb);
int getNumMatches(Mat mrGr, Mat mRgb, vector<KeyPoint> keyPoints_esc, Mat descriptores_esc, int nObjeto);
void LiberaEscenario();
int encuentraObjeto(Mat mrGr, Mat mRgb, vector<KeyPoint> keyPoints_esc, Mat descriptores_esc, int nObjeto);





#endif /* LIBRERIA_H_ */
