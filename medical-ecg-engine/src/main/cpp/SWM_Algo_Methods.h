#pragma once
#include "File_FileScan.h"
#include <vector>
using namespace std;


typedef struct _DNN_Strutcure
{
	//float inputdata;
	// DNN parameter define
	vector <vector<float>> DNN_W1, DNN_W2, DNN_W3;
	vector <vector<float>> DNN_b1, DNN_b2, DNN_b3;
	vector <vector<float>> mu,sigma;
	/*
	void DNN_Structure()
	{	
		FileReading("Layer1_W.txt",&DNN_W1,150,87);
		FileReading("Layer2_W.txt",&DNN_W2,150,150);
		FileReading("Layer3_W.txt",&DNN_W3, 4, 150);
		FileReading("Layer1_b.txt",&DNN_b1,150,1);
		FileReading("Layer2_b.txt",&DNN_b2,150,1);
		FileReading("Layer3_b.txt",&DNN_b3,4,1);
		
	};
	*/
}DNN_Net_HeartBeatClassification;

