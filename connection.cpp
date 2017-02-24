#include "connection.h"
using namespace std;
using namespace cv;


Mat hwnd2mat(HWND hwnd){

	HDC hwindowDC, hwindowCompatibleDC;

	int height, width, srcheight, srcwidth;
	HBITMAP hbwindow;
	Mat src;
	BITMAPINFOHEADER  bi;

	hwindowDC = GetDC(hwnd);
	hwindowCompatibleDC = CreateCompatibleDC(hwindowDC);
	SetStretchBltMode(hwindowCompatibleDC, COLORONCOLOR);

	RECT windowsize;    // get the height and width of the screen
	GetClientRect(hwnd, &windowsize);

	srcheight = windowsize.bottom;
	srcwidth = windowsize.right;
	height = windowsize.bottom / 2;  //change this to whatever size you want to resize to
	width = windowsize.right / 2;

	src.create(height, width, CV_8UC4);

	// create a bitmap
	hbwindow = CreateCompatibleBitmap(hwindowDC, width, height);
	bi.biSize = sizeof(BITMAPINFOHEADER);    //http://msdn.microsoft.com/en-us/library/windows/window/dd183402%28v=vs.85%29.aspx
	bi.biWidth = width;
	bi.biHeight = -height;  //this is the line that makes it draw upside down or not
	bi.biPlanes = 1;
	bi.biBitCount = 32;
	bi.biCompression = BI_RGB;
	bi.biSizeImage = 0;
	bi.biXPelsPerMeter = 0;
	bi.biYPelsPerMeter = 0;
	bi.biClrUsed = 0;
	bi.biClrImportant = 0;

	// use the previously created device context with the bitmap
	SelectObject(hwindowCompatibleDC, hbwindow);
	// copy from the window device context to the bitmap device context
	StretchBlt(hwindowCompatibleDC, 0, 0, width, height, hwindowDC, 0, 0, srcwidth, srcheight, SRCCOPY); //change SRCCOPY to NOTSRCCOPY for wacky colors !
	GetDIBits(hwindowCompatibleDC, hbwindow, 0, height, src.data, (BITMAPINFO *)&bi, DIB_RGB_COLORS);  //copy from hwindowCompatibleDC to hbwindow

	// avoid memory leak
	DeleteObject(hbwindow); DeleteDC(hwindowCompatibleDC); ReleaseDC(hwnd, hwindowDC);

	return src;
}

Mat ditoreted(Mat input)
{
	Mat image, imag;
	//image = imread(argv[1], IMREAD_COLOR); // Read the file
	//imag = imread(argv[1], IMREAD_COLOR); // Read the file


	Mat cammatrix = cv::Mat::zeros(3, 3, CV_32F);
	cammatrix.at<float>(0, 0) = 3.7089418826568277e+002;
	cammatrix.at<float>(1, 1) = 3.7179355652545451e+002;
	cammatrix.at<float>(0, 2) = 3.4450520804288089e+002;
	cammatrix.at<float>(1, 2) = 2.5859133287932718e+002;
	cammatrix.at<float>(2, 2) = 1.0;

	Mat k = cv::Mat::zeros(4, 1, CV_32F);
	k.at<float>(0) = 1;
	k.at<float>(1) = 2;
	k.at<float>(2) = 1;
	k.at<float>(3) = 2;
	int foo[4] = { 1, 1, 1, 1 };
	vector<int> kk(4, 1);
	cout << " before fish eyey" << endl;
	cout << cammatrix.size() << endl;
	cout << k.size() << endl;
	fisheye::undistortImage(input, image, cammatrix, k, cammatrix, image.size());
	cout << " after fishyey" << endl;

	return image;

}


int pic(int sock)
{
		/*
		std::string sendMsg = "frame starts \r\n";

		int amt = send(sock, sendMsg.c_str(), sendMsg.size(), 0);
			closesocket(sock);
			*/
		int x_size = 800, y_size = 800; // <-- Your res for the image
		

		HBITMAP hBitmap; // <-- The image represented by hBitmap
		Mat matBitmap; // <-- The image represented by mat



		// Initialize DCs
		HDC hdcSys = GetDC(NULL); // Get DC of the target capture..
		HDC hdcMem = CreateCompatibleDC(hdcSys); // Create compatible DC 




		void *ptrBitmapPixels; // <-- Pointer variable that will contain the potinter for the pixels




		// Create hBitmap with Pointer to the pixels of the Bitmap
		BITMAPINFO bi; HDC hdc;
		ZeroMemory(&bi, sizeof(BITMAPINFO));
		bi.bmiHeader.biSize = sizeof(BITMAPINFOHEADER);
		bi.bmiHeader.biWidth = x_size;
		bi.bmiHeader.biHeight = -y_size;  //negative so (0,0) is at top left
		bi.bmiHeader.biPlanes = 1;
		bi.bmiHeader.biBitCount = 32;
		hdc = GetDC(NULL);
		hBitmap = CreateDIBSection(hdc, &bi, DIB_RGB_COLORS, &ptrBitmapPixels, NULL, 0);
		// ^^ The output: hBitmap & ptrBitmapPixels

		while (true)
		{
		// Set hBitmap in the hdcMem 
		SelectObject(hdcMem, hBitmap);

		// Set matBitmap to point to the pixels of the hBitmap
		matBitmap = Mat(y_size, x_size, CV_8UC4, ptrBitmapPixels, 0);
		//              ^^ note: first it is y, then it is x. very confusing

		// * SETUP DONE *

		// Now update the pixels using BitBlt
		BitBlt(hdcMem, 0, 0, x_size, y_size, hdcSys, 0, 0, SRCCOPY);


		// Just to do some image processing on the pixels.. (Dont have to to this)
		//Mat matRef = matBitmap(Range(100, 200), Range(100, 200));
		//                            y1    y2          x1   x2
		//bitwise_not(matBitmap, matBitmap); // Invert the colors in this x1,x2,y1,y
		// Display the results through Mat

		//imshow("Title", ditoreted(matBitmap));

		pyrDown(matBitmap, matBitmap, Size(matBitmap.cols / 2, matBitmap.rows / 2), BORDER_DEFAULT);

		Mat cammatrix = getRotationMatrix2D(Point2f(200, 200), 270, 1);
		warpAffine(matBitmap, matBitmap, cammatrix, matBitmap.size(), INTER_LINEAR, BORDER_DEFAULT, 0);
		//transform(matBitmap, matBitmap, cammatrix);

		Mat mati = ditoreted(matBitmap);
		
		//Gdiplus::Bitmap Bitmap(800, 600, mati.step1(), PixelFormat24bppRGB, mati.data);
		mati = (mati.reshape(0, 1));
		int  imgSize = mati.total()*mati.elemSize();
		//cout << mati.checkVector(0,-1,true) << endl;
		//bytes = send(clientSock, frame.data, imgSize, 0))
		int amt = send(sock, (const char*)mati.data, imgSize, 0);
		

		//int amt = send(sock, Bitmap, imgSize, 0);
		printf("img size %d bytes.\n", imgSize);
		printf("Send %d bytes.\n", amt);
		printf("mati size %d bytes.\n", mati.size());
		printf("mati size %d bytes.\n", mati.total());
		mati.~Mat();
		
		char c = cvWaitKey(33);
		if (c == 27) break;
	}
		closesocket(sock);
	// Wait until some key is pressed
	waitKey(0);

	return 0;
}