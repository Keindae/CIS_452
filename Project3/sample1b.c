// is this Sample Program 1?<stdlib.h>
#include <fcntl.h>
#include <sys/types.h>
#include <unistd.h>
#include <errno.h>

   #define SIZE 30

   int main(int argc, char *argv[])
   {
      struct flock fileLock;
      int fd;
      char buf[SIZE] = "// is this Sample Program 1?";



      if (argc < 2) {
         printf ("usage: filename\n");
         exit (1);
      }
      if ((fd = open (argv[1], O_RDWR)) < 0) {
         perror ("there is");
         exit (1);
      }

      fcntl(fd, F_GETLK, &fileLock);
      while(fileLock.l_type != F_UNLCK){
        sleep(1);
      }


      printf("unlocked");
      close(fd);

      return 0;
   }
