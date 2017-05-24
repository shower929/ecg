// NOTE:   
// write by Chih-Sheng (Tommy) Huang, 2/14/2017 
// version 1.0

/* 
/////  function list /////
	MyMath(T A[], int newendsize); //create a class with all element of array (A)
	~MyMath(); // free the data
	int findminIndex();
	T findMinValue();
	int findmaxIndex();
	T findMaxValue();
	T Math_Sum();
	T Math_Mean();
	T Statistic_Variane();
	T Statistic_SampleVariane();
	T Statistic_StandardDeviation();
	T Statistic_SampleStandardDeviation();
	T Statistic_Skewness();
	T Statistic_Kurtosis();
	T Statistic_Covariane(MyMath& left, MyMath& right);
	T Math_Vector_EuclideanDistance(MyMath& left, MyMath& right);// Euclidean distance between two vectors
	T Math_pNorms(int p);// p-norm
	void Math_Vector_ZeroMean(); //// zero mean of a vecotr
	void Statistic_Zsore();//z-sore

//////////////////////////////////////////////////

Example: 
	float A[5]={1,3,5,7,8};
	MyMath <float> B(A);

	int c=B.findmaxIndex(); // c: the index of maxiumu value of A[];
	float b=B.Math_Sum();// b: sum(A[];
*/


#pragma once
#include <vector>
#include <math.h>
using namespace std;

template <typename T> class MyMath
{
public:
	vector<T> data; 
	int size;


	MyMath(vector <T> A)
	{
		//data=A;
		size=A.size();
		(data).resize(size);
		for (int i=0; i<size;i++)
			(data).at(i) = A[i];
		
	};
	~MyMath() 
	{ 
		(data).clear();
	};


	int findminIndex(){
		int minIndex=-1;
		for (int i=0; i<size;i++)
			if (minIndex<0 || data[i]<data[minIndex])
				minIndex=i;
		return minIndex;
	};
	
	T findMinValue() {
		T minValue = data[0];
		for(int i= 0; i< size; i++)
			if(data[i] < minValue)
				minValue= data[i];
		return	minValue;
	};
	
	int findmaxIndex(){
		int maxIndex=-1;
		for (int i=0; i<size;i++)
			if (maxIndex<0 || data[i]>data[maxIndex])
				maxIndex=i;
		return maxIndex;
	};
	
	T findMaxValue() {
		T maxValue= data[0];
		for(int i = 0; i< size; i++)
			if(data[i] > maxValue)
				maxValue= data[i];
		return	maxValue;
	};


	T Math_Sum(){
		T sum = 0;
		for(int i = 0; i < size; i++)
		{
			sum = sum+ data[i];
		}
		return sum;
	};

	T Math_Mean(){
		T sum= Math_Sum();
		return (sum /size);
	};

	T Statistic_Variane(){
		T mean= Math_Mean();
		T temp = 0;
		for(int i = 0; i < size; i++)
			temp += pow((data[i] - mean), 2);
    
		return temp / (size);
	};

	T Statistic_SampleVariane(){
		T mean= Math_Mean();
		T temp = 0;
		for(int i = 0; i < size; i++)
			temp += pow((data[i] - mean), 2) ;
    
		return temp / (size-1) ;
	};

	T Statistic_StandardDeviation(){
		return sqrt(Statistic_Variane());
	};

	T Statistic_SampleStandardDeviation()
	{
		return sqrt(Statistic_SampleVariane());
	};

	T Statistic_Skewness(){
		T mean= Math_Mean();
		T std= Statistic_SampleStandardDeviation();
 
		T k3 = 0;
		T k2 = 0;
		for(int i = 0; i < size; i++)
		{
			k3 += pow((data[i] - mean), 3);
			k2 += pow((data[i] - mean), 2);
		}
		k3=k3/size;
		k2=k2/size;
		return k3/pow(k2,T(1.5));
	};

	T Statistic_Kurtosis(){
		T mean= Math_Mean();
		T std= Statistic_StandardDeviation();
		T temp = 0;
		for(int i = 0; i < size; i++)
			temp += pow((data[i] - mean), 4)/pow(std,4);
    
		return temp / (size);
	};

	T Statistic_Covariane(MyMath& left, MyMath& right){
		leftsize = left.size;
		rightsize = right.size;
		if leftsize!=rightsize return 0;
		
		T mean1= left.Math_Mean();
		T mean2= right.Math_Mean();
		T tmp=0;
		for(int i=0; i<size; i++)
			tmp+=(left.data[i]-mean1)*(right.data[i]-mean2);	
	
		return tmp/size;
	};

	// Euclidean distance between two vectors
	T Math_Vector_EuclideanDistance(MyMath& left, MyMath& right){
		leftsize = left.size;
		rightsize = right.size;
		if leftsize!=rightsize return 0;

		T tmp=0;
		for(int i = 0; i < dimension; i++)
			tmp+=pow(left.data[i]-right.data[i],2);
    
		return sqrt(tmp);
	}

	T Math_pNorms(int p){
		T temp = 0, pNorm=0, doublep=0;
		doublep=(1/T(p));
		if (p==1)
		{
			for(int i = 0; i < size; i++)
				temp += abs(data[i]);
		
			pNorm=temp;
		}
		else
		{
			for(int i = 0; i < size; i++)
			{
				temp += pow(abs(data[i]) , p);
			}
			pNorm=pow(temp, doublep);
		}
		return pNorm;
	};

	// zero mean of a vecotr
	void Math_Vector_ZeroMean()
	{
		T mean= Math_Mean();
		for(int i = 0; i < size; i++)
			data[i]=data[i]-mean;
    
	};

	//z-sore
	void Statistic_Zsore(){
	T Mu=Math_Mean();
	T Sigma=Statistic_SampleStandardDeviation();

	for (int i=0; i<size; i++)
		data[i]=(data[i]-Mu) / Sigma;
	
}


};


