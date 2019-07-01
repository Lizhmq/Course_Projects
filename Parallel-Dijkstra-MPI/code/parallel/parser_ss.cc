// !!! This parser has no error diagnostics !!!
/* files to be included: */

#include <stdlib.h>
#include <string.h>
#include <stdio.h>

int parse_ss(long *sN_ad, long **source_array, char *aName)

// long    *sN_ad;                 /* address of the number of sources */
// long    **source_array;        /* pointer to the array of sources */
{

#define MAXLINE       100	/* max line length in the input file */
#define P_FIELDS        4       /* no of fields in problem line */
#define AUX_TYPE "aux"          /* denotes auxilary file */
#define PROBLEM_TYPE "sp"       /* name of problem type*/
#define PROBLEM_VAR "ss"         /* single-source */

  long    n;                      /* internal number of sources */
  long    k;
  long   *sources=NULL;           /* internal pointer to sources */
  long source;
  char prA_type[4], pr_type[3], pr_var[3], in_line[MAXLINE];
  long no_lines= 0, no_plines=0, no_slines=0;
  FILE *aFile;


/* The main loop:
        -  reads the line of the input,
        -  analises its type,
        -  puts data to the arrays,
        -  does service functions
*/

 aFile = fopen(aName, "r");
 if (aFile == NULL) {
   fprintf(stderr, "ERROR: file %s not found\n", aName);
   exit(1);
 }

while (fgets(in_line, MAXLINE, aFile) != NULL)
  {
  no_lines ++;


  switch (in_line[0])
    {
    case 'c':                  /* skip lines with comments */
    case '\n':                 /* skip empty lines   */
    case '\0':                 /* skip empty lines at the end of file */
      break;
      
    case 'p':                  /* problem description      */
      if ( no_plines > 0 )
	/* more than one problem line */
	{ goto error; }
      
      no_plines = 1;
      
      if (
	  /* reading problem line: type of problem, no of nodes, no of arcs */
	  sscanf( in_line, "%*c %s %s %s %ld", 
		  prA_type, pr_type, pr_var, &n )
	  != P_FIELDS
	  )
	/*wrong number of parameters in the problem line*/
	{goto error; }

      if ( strcmp ( prA_type, AUX_TYPE ) )
	/*not aux file*/
	{goto error; }
      
      if ( strcmp ( pr_type, PROBLEM_TYPE ) )
	/*wrong problem type*/
	{goto error; }
      
      if ( strcmp ( pr_var, PROBLEM_VAR ) )
	/*wrong problem variant*/
	{goto error; }
      
      /* allocating memory for  'nodes', 'arcs'  and internal arrays */
      sources  = (long *) calloc(n+1, sizeof(long));

      break;
    case 's':		         /* source description */
      no_slines++;
      if ( no_plines == 0 )
	/* there was not problem line above */
	{ goto error; }

      /* reading source */
      k = sscanf ( in_line,"%*c %ld", &source );
      
      if ( k < 1 )
	/* source number is not red */
	{ goto error; }
      
      sources[no_slines-1] = source;
      
      break;
    default:
      /* unknown type of line */
      goto error;
      break;
      
    } /* end of switch */
}     /* end of input loop */


if ( feof (aFile) == 0 ) /* reading error */
  { goto error; } 

if ( no_lines == 0 ) /* empty input */
  { goto error; } 

 *sN_ad = n;
 *source_array = sources;

 return (0);

/* ---------------------------------- */
 error:  /* error found reading input */

 fprintf ( stderr, "Error parsing auxilarly file: line %ld: %s\n", 
	   no_lines, in_line);

exit (1);

}
/* --------------------   end of parser  -------------------*/
