#pragma once
#include <vector>
using namespace std;

template <typename T> 
int Matrix_multiplication(vector<vector<T>> X,vector<vector<T>> Y, vector<vector<T>>* Result); 
// http://blog.jobbole.com/90310 
// return 1: success implement; return 0:fail implement.  



template <typename T> 
int Matrix_multiplication(vector<vector<T>> X,vector<vector<T>> Y, vector<vector<T>>* Result)  
{ 
	// X:  n_x * p_x
	// Y:  n_y * p_y
	// Result=X*Y: n_x * p_y;
    int n_x,p_x,n_y,p_y;
	n_x=X.size();
	p_x=X[0].size();
	n_y=Y.size();
	p_y=Y[0].size();

	if (p_x!=n_y)
	{
		return 0;
	}

	(*Result).resize(n_x);// 
	for(int i=0; i!=n_x; ++i) (*Result)[i].resize(p_y);

    for(int i=0;i<n_x;i++) 
        for(int k=0;k<p_x;k++) 
            if(X[i][k]) 
            for(int j=0;j<p_y;j++) 
                (*Result)[i][j]=((*Result)[i][j]+X[i][k]*Y[k][j]);
	
	return 1;
};

template <typename T> 
void Matrix_Transpose(vector<vector<T>> X, vector<vector<T>>* Result)  
{ 
	// X: n * p
	// Result=p * n;
    int n,p;
	n=X.size();
	p=X[0].size();
	(*Result).resize(p);// 
	for(int i=0; i!=p; ++i) (*Result)[i].resize(n);

    for(int i=0;i<n;i++) 
        for(int j=0;j<p;j++) 
           (*Result)[j][i]=X[i][j];

};



