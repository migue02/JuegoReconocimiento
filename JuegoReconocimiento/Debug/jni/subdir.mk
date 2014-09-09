################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../jni/JuegoPrueba.cpp \
../jni/PatRecModificado.cpp \
../jni/ReconocimientoPrueba.cpp 

OBJS += \
./jni/JuegoPrueba.o \
./jni/PatRecModificado.o \
./jni/ReconocimientoPrueba.o 

CPP_DEPS += \
./jni/JuegoPrueba.d \
./jni/PatRecModificado.d \
./jni/ReconocimientoPrueba.d 


# Each subdirectory must supply rules for building sources it contributes
jni/%.o: ../jni/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: Cygwin C++ Compiler'
	g++ -I"C:/Users/Migue/git/JuegoReconocimiento/JuegoReconocimiento/../../OpenCV-2.4.5-Tegra-sdk-r2/sdk/native/jni/" -I"C:\NVPACK\android-ndk-r9b/sources/cxx-stl/gnu-libstdc++/4.6/libs/armeabi-v7a/include" -I"C:\NVPACK\android-ndk-r9b/sources/cxx-stl/gnu-libstdc++/4.6/include" -I"C:\NVPACK\android-ndk-r9b/platforms/android-9/arch-arm/usr/include" -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


