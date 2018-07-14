package certi.filesystem.kata;

import java.util.Scanner;

public class UserSolution {
	// extern int mkdir(char name[]);
	// extern int cd(char path[]);
	// extern int rm(char path[]);
	// extern int ls(char path[]);
	static int t, s, n;
	static int type[] = new int[40210];
	static int mx[] = { 0, 20000, 10000, 10000, 200 };
	static char cmd[][] = { "NULL".toCharArray(), "mkdir".toCharArray(), "cd".toCharArray(), "rm".toCharArray(),
			"ls".toCharArray() };

	int _strlen(char str[]) {
		int ret = 0;
		while (str[ret] != 0)
			ret++;
		return ret;
	}

	int _strcmp(char str1[], char str2[]) {
		int pos = 0;
		while (str1[pos] != 0 && str2[pos] != 0 && str1[pos] == str2[pos])
			pos++;
		if (str1[pos] != 0 && str2[pos] != 0)
			return 0;
		if (str1[pos] != 0)
			return -1;
		if (str2[pos] != 0)
			return 1;
		return str1[pos] - str2[pos];
	}

	void _strcpy(char dst[], char src[]) {
		int pos = 0;
		while (src[pos] != 0) {
			dst[pos] = src[pos];
			pos++;
		}
		dst[pos] = 0;
	}

	void _strcat(char dst[], char src[]) {
		int index = _strlen(dst);
		int len = _strlen(src);
		for (int l = 0; l < len; l++)
			dst[index++] = src[l];

	}

	static void _swap(int a, int b) {
		int tmp = a;
		a = b;
		b = tmp;
	}
	//
	// public static void main(String args[])
	// {
	//
	// Scanner scan = new Scanner(System.in);
	//
	// int i,j,k,x,l;
	// char arg[] = new char[20];
	// t= scan.nextInt();
	// for(i=1;i<=t;++i){
	// x=0;
	// for(j=1;j<=4;++j)
	// for(k=1;k<=mx[j];++k)
	// type[++x]=j;
	//
	// for(j=1;j<=40200;++j)
	// _swap(type[j],type[(int) (Math.random()%40200+1)]);
	// Solution.init();
	// s = scan.nextInt();
	// n = scan.nextInt();
	// srand(s);
	//
	//
	// printf("#%d\n",i);
	// for(j=1;j<=n;++j)
	// {
	// if(s==0)scanf("%d%s",&x,arg);
	// else
	// {
	// x=type[j];
	// l=rand()%2;
	// if((x==1||x==2)&&l==0)l=1;
	// for(k=0;k<l;++k)arg[k]=rand()%26+'a';
	// arg[k]=0;
	// if(x==2)
	// {
	// if(!rand()%1000)_strcpy(arg,"/");
	// if(!rand()%10)_strcpy(arg,"..");
	// }
	// }
	// if(x==2&&_strcmp(arg,"/")&&_strcmp(arg,".."))_strcat(arg,"\t");
	// if(x==3||x==4)
	// {
	// if(arg[0]=='0')arg[0]=0;
	// _strcat(arg,"*");
	// }
	// printf("%d\n",(*cmd[x])(arg));
	// }
	// }
	// return 0;
	// }
	//
}
