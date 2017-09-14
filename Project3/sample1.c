#include <stdlib.h>
#include <fcntl.h>
#include <sys/types.h>
#include <unistd.h>
#include <errno.h>
#include <stdio.h>

   #define SIZE 30

   int main(int argc, char *argv[])
   {
      struct flock fileLock;
      int fd;
      char buf[SIZE] = "// is this Sample Program 1?";

      int waiting = 0;
      while (fcntl (fd, F_SETLK, &fileLock) == 0) {
       	if (waiting == 0){
           		waiting++;
           		printf("Waiting for file lock to be released...\n");
       	}
         }
         if (waiting == 1){
       	printf("File lock released!\n");
       	char change[10] = "changes to";
       	int i;
       	for(i = 0; i < 10; i++){
            buf[i] = change[i];
       	}
         }

      if (argc < 2) {
         printf ("usage: filename\n");
         exit (1);
      }
      if ((fd = open (argv[1], O_RDWR)) < 0) {
         perror ("there is");
         exit (1);
      }

      fileLock.l_type = F_WRLCK;
      fileLock.l_whence = SEEK_SET;
      fileLock.l_start = 0;
      fileLock.l_len = 0;
      if (fcntl (fd, F_SETLK, &fileLock) < 0) {
         perror ("no way");
         exit (1);
      }

      write (fd, buf, SIZE-2);
      printf('gothur');
      sleep (10);

      close(fd);

      return 0;
   }
