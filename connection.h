///#include <stdafx.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/calib3d.hpp>
#include <opencv2/imgcodecs.hpp>
#include <Windows.h>

#include <iostream>
#include <string>
using namespace std;
using namespace cv;

Mat hwnd2mat(HWND hwnd);

Mat ditoreted(Mat input);


int pic(int sock);