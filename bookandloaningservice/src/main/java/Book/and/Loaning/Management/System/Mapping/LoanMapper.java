package Book.and.Loaning.Management.System.Mapping;
import Book.and.Loaning.Management.System.DTO.LoanDTO;
import Book.and.Loaning.Management.System.Entity.LoanEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "bookId", source = "bookId")
    @Mapping(target = "borrowerID", source = "borrowerID")
    LoanDTO toDTO(LoanEntity entity);

    @Mapping(target = "bookId", source = "bookId")
    @Mapping(target = "borrowerID", source = "borrowerID")
    LoanEntity toEntity(LoanDTO dto);
}