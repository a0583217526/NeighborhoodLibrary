package Book.and.Loaning.Management.System.Exceptions;


public class BookServiceException extends RuntimeException {

   public BookServiceException(String massage){
       super(massage);
   }
}
