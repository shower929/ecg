

#include <vector>
#include "SWM_Algo_API.h"
using namespace std;

void notchFilter_60Hz_sr250Hz(vector <float> x,vector <float>* y, long ndata)
{
	float bn[3]={0.645263428365958,-0.0810328518007290,0.645263428365958};
	float an[3]={1,-0.0810328518007290,0.290526856731916};
	for (int n=0;n<ndata;n++)
	{
		if (n==0)
			(*y)[n]=bn[0]*x[n];
		else if (n==1)
			(*y)[n]=bn[0]*x[n]+bn[1]*x[n-1]-an[1]*(*y)[n-1];
		else
			(*y)[n]=bn[0]*x[n]+bn[1]*x[n-1]+bn[2]*x[n-2]-an[1]*(*y)[n-1]-an[2]*(*y)[n-2];
	}
}

void notchFilter_60Hz_sr360Hz(vector <float> x,vector <float>* y, long ndata)
{
	float bn[3]={0.733153829077499,-0.733153829077499,0.733153829077499};
	float an[3]={1,-0.733153829077499,0.466307658154999};
	for (int n=0;n<ndata;n++)
	{
		if (n==0)
			(*y)[n]=bn[0]*x[n];
		else if (n==1)
			(*y)[n]=bn[0]*x[n]+bn[1]*x[n-1]-an[1]*(*y)[n-1];
		else
			(*y)[n]=bn[0]*x[n]+bn[1]*x[n-1]+bn[2]*x[n-2]-an[1]*(*y)[n-1]-an[2]*(*y)[n-2];
	}
}

void FIR_LP_250Hz(vector <float> x,vector <float>* y, long ndata)
{
	float bn[11]={0.0194041444581080,0.0450147864686318,0.0815814643972139,0.119825032244859,0.149014391144678,0.159968759587586,0.149014391144678,0.119825032244859,0.0815814643972139,0.0450147864686318,0.0194041444581080};
	float tmp=0;
	for (int n=0;n<ndata;n++)
	{
		if (n<10)
		{
			tmp=0;
			for (int i=0;i<n+1;i++)
				tmp+= bn[i]*x[n-i];

			(*y)[n]=tmp;
		}
		else
		{
			tmp=0;
			for (int i=0;i<11;i++)
				tmp+= bn[i]*x[n-i];

			(*y)[n]=tmp;
		}
	}
}

void FIR_LP_360Hz(vector <float> x,vector <float>* y, long ndata)
{
	float bn[17]={0.00895567203488443,0.0167898345946794,0.0295617071851600,0.0454324200454105,0.0629696314718780,0.0801389042614653,0.0946360340878712,0.104339082954544,0.107753910440413,0.104339082954544,0.0946360340878712,0.0801389042614653,0.0629696314718780,0.0454324200454105,0.0295617071851600,0.0167898345946794,0.00895567203488443};
	
	float tmp=0;
	for (int n=0;n<ndata;n++)
	{
		if (n<17)
		{
			tmp=0;
			for (int i=0;i<n+1;i++)
				tmp+= bn[i]*x[n-i];

			(*y)[n]=tmp;
		}
		else
		{
			tmp=0;
			for (int i=0;i<17;i++)
				tmp+= bn[i]*x[n-i];

			(*y)[n]=tmp;
		}
	}
}



void BaselineWanderFilter_250Hz(vector <float> x,vector <float>* y, long ndata)
{
	float bn[2]={0.019011975760990, 0};
	float an[2]={1, -0.980988024239010};
	float si=0.980988024239010;
	vector <float> v;
	v.resize(ndata+6); // allocate a tmp memory
	float tmp=0;
	long n=0;

	v[0]=2*x[0]-x[3];
	v[1]=2*x[0]-x[2];
	v[2]=2*x[0]-x[1];
	for (int i=0; i<ndata; i++)
	{
		v[i+3]=x[i];
	}
	v[ndata+5]=2*x[ndata-1]-x[ndata-2];
	v[ndata+4]=2*x[ndata-1]-x[ndata-3];
	v[ndata+3]=2*x[ndata-1]-x[ndata-4];



	for (n=0;n<ndata+6;n++)
	{
		if (n==0)
			v[n]=bn[0]*v[n];
		else
			v[n]=bn[0]*v[n]+bn[1]*v[n-1]-an[1]*v[n-1];
	}
	
	n=ndata+5;
	do
	{
		if (n==ndata+5)
			v[n]=bn[0]*v[n];
		else
			v[n]=bn[0]*v[n]+bn[1]*v[n+1]-an[1]*v[n+1];

		if ((n>=3) && (n<=(ndata+2)))
		{(*y)[n-3]=x[n-3]-v[n];} // V:Baseline Wander

	} while(n--);

	v.clear();
}


void BaselineWanderFilter_360Hz(vector <float> x,vector <float>* y, long ndata)
{
	float bn[2]={0.013241579947798,0};
	float an[2]={1,-0.98675842005220};
	float si=0.986758420052202;
	vector <float> v;
	v.resize(ndata+6); // allocate a tmp memory
	float tmp=0;
	long n=0;

	v[0]=2*x[0]-x[3];
	v[1]=2*x[0]-x[2];
	v[2]=2*x[0]-x[1];
	for (int i=0; i<ndata; i++)
	{
		v[i+3]=x[i];
	}
	v[ndata+5]=2*x[ndata-1]-x[ndata-2];
	v[ndata+4]=2*x[ndata-1]-x[ndata-3];
	v[ndata+3]=2*x[ndata-1]-x[ndata-4];



	for (n=0;n<ndata+6;n++)
	{
		if (n==0)
			v[n]=bn[0]*v[n];
		else
			v[n]=bn[0]*v[n]+bn[1]*v[n-1]-an[1]*v[n-1];
	}
	
	n=ndata+5;
	do
	{
		if (n==ndata+5)
			v[n]=bn[0]*v[n];
		else
			v[n]=bn[0]*v[n]+bn[1]*v[n+1]-an[1]*v[n+1];

		if ((n>=3) && (n<=(ndata+2)))
		{(*y)[n-3]=x[n-3]-v[n];} // V:Baseline Wander

	} while(n--);

	v.clear();
}



void SignalEnhance_2D(vector <float> x,vector <float>* y, long ndata)
{
	// (Teager-Kaiser energy operator (TKEO))
	for (int i=1;i<ndata-1;i++)
	{
		(*y)[i]=x[i]*x[i]-x[i-1]*x[i+1];
	}
	(*y)[0]=(*y)[1];
	(*y)[ndata-1]=(*y)[ndata-2];

}



void MeanFilter(vector <float> x,vector <float>* y,long ndata, int filtersize)
{
	int WindowSize_Left=0;
	int WindowSize_Right=0;
	float tmp=0;
	long count=0;

	if((filtersize%2)==0)
	{
		WindowSize_Left=(filtersize/2);
		WindowSize_Right=(filtersize/2)-1;
	}
	else
	{
		WindowSize_Left=(filtersize-1)/2;
		WindowSize_Right=(filtersize-1)/2;
	}
	
	for (int i=0;i<ndata;i++)
	{
		tmp=0;
		count=0;
		if  ((i >= WindowSize_Left) && (i <= ndata-WindowSize_Right-1)) 
		{
			for (int j=i-WindowSize_Left;j<=i+WindowSize_Right;j++)
			{
				tmp+=x[j];
				count++;
			}
			(*y)[i]=tmp/count;
		}
		
		else if (i < WindowSize_Left)
		{
			for (int j=0;j<i+WindowSize_Right;j++)
			{
				tmp+=x[j];
				count++;
			}
			(*y)[i]=tmp/count; 
		}

		else if (i > ndata-WindowSize_Right-1)
		{
			for (int j=i-WindowSize_Left;j<ndata;j++)
			{
				tmp+=x[j];
				count++;
			}
			(*y)[i]=tmp/count; 
		}
		
	}
}



void MeidanFilter(vector <float> x,vector <float>* y,long ndata, int filtersize)
{
	int WindowSize_Left=0;
	int WindowSize_Right=0;
	float tmp=0;
	long count=0;
	float *tmparrary;
	int c=0;

	tmparrary = (float*) malloc (sizeof(float)*filtersize); 

	if((filtersize%2)==0)
	{
		WindowSize_Left=(filtersize/2);
		WindowSize_Right=(filtersize/2)-1;
	}
	else
	{
		WindowSize_Left=(filtersize-1)/2;
		WindowSize_Right=(filtersize-1)/2;
	}
	
	for (int i=0;i<ndata;i++)
	{
		tmp=0;
		count=0;
		if  ((i >= WindowSize_Left) && (i <= ndata-WindowSize_Right-1)) 
		{
			c=0;
			for (int j=i-WindowSize_Left;j<=i+WindowSize_Right;j++)
				tmparrary[c++]=x[j];

			(*y)[i]=EvaluateMedian(tmparrary, c);
		}
		
		else if (i < WindowSize_Left)
		{
			c=0;
			for (int j=0;j<i+WindowSize_Right;j++)
				tmparrary[c++]=x[j];
			
			(*y)[i]=EvaluateMedian(tmparrary, c); 
		}

		else if (i > ndata-WindowSize_Right-1)
		{
			c=0;
			for (int j=i-WindowSize_Left;j<ndata;j++)
				tmparrary[c++]=x[j];
			
			(*y)[i]=EvaluateMedian(tmparrary, c);
		}	
	}


}




int QuickSortOnce(float data[], int low, int high)  
{    
    float pivot = data[low];  
    int i = low, j = high;  
  
    while (i < j)  
    {  
        // 从右到左，寻找首个小于pivot的元素。  
        while (data[j] >= pivot && i < j)  
            j--;  
        
        // 执行到此，j已指向从右端起首个小于或等于pivot的元素。  
        // 执行替换。  
        data[i] = data[j];  

        // 从左到右，寻找首个大于pivot的元素。  
        while (data[i] <= pivot && i < j)  
            i++;  
        
        // 执行到此，i已指向从左端起首个大于或等于pivot的元素。  
        // 执行替换。  
        data[j] = data[i];  
    }  
  
    // 退出while循环,执行至此,必定是i=j的情况。  
    // i（或j）指向的即是枢轴的位置，定位该趟排序的枢轴并将该位置返回。  
    data[i] = pivot;  
    return i;  
}  
void QuickSort(float data[], int low, int high)  
{  
    if (low >= high)  
    {  
        return;  
    }  
    int pivot = QuickSortOnce(data, low, high);  
    // 对枢轴的左端进行排序。  
    QuickSort(data, low, pivot - 1);  
    // 对枢轴的右端进行排序。  
    QuickSort(data, pivot + 1, high);  
}  
  
float EvaluateMedian(float data[], int ndata)  
{  
    QuickSort(data, 0, ndata- 1);  
  
    if(ndata % 2 !=0)  
    {  
        return data[ndata / 2];  
    }  
    else  
    {  
        return (data[ndata / 2] + data[ndata / 2 - 1]) / 2;  
    }  
}  