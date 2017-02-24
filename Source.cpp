#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <winsock2.h>
#include <Ws2tcpip.h>
#include <errno.h>
#include "connection.h"
void connection(int sock)
{
	// Pretty much your C++ code verbatim.
	std::string sendMsg = "This is a test \r\n";
	
	int amt = send(sock, sendMsg.c_str(), sendMsg.size(), 0);

	printf("Send %d bytes.\n", amt);
	closesocket(sock);
}
using namespace std;
int main()
{
	int sock, csock;
	struct sockaddr_in sin;
	char *host = "192.168.43.16";
	unsigned short port = 8888;

	char text[] = "192.168.1.100";
	wchar_t wtext[20];
	std::mbstowcs(wtext, text, strlen(text) + 1);//Plus null
	LPWSTR ptr = wtext;


	WSADATA wsaData;
	int listenFd;
	cout << "hey" << endl;
	if (WSAStartup(MAKEWORD(1, 1), &wsaData) == -1) {
		printf("Error initialising WSA.\n");
		return -1;
	}

	if ((sock = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
		printf("Error %d opening socket.\n", errno);
		perror("socket");
		exit(EXIT_FAILURE);
	}

	sin.sin_family = AF_INET;
	sin.sin_port = htons(port);


	if (InetPton(AF_INET, ptr, &sin.sin_addr) != 1) {
		perror("inet_pton");
		exit(EXIT_FAILURE);
	}

	if (bind(sock, (struct sockaddr*) &sin, sizeof(sin)) != 0) {
		printf("Error %d opening socket.\n", errno);
		perror("bind");
		exit(EXIT_FAILURE);
	}
	
	cout << "you" << endl;

	if (listen(sock, SOMAXCONN) != 0) {
		perror("listen");
		exit(EXIT_FAILURE);
	}
	cout << "conected" << endl;
	if ((csock = accept(sock, NULL, NULL)) == -1) {
		cout << "sock" << endl;
		perror("accept");
		exit(EXIT_FAILURE);
	}

	cout << "yet" << endl;
	//connection(csock);
	pic(csock);
	closesocket(sock);
	return EXIT_SUCCESS;
}


