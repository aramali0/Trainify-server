package e_learning.services.ServiceImpl;

import e_learning.DTO.ResponseDto;
import e_learning.entity.Response;
import e_learning.mappers.mappersImpl.ResponseMapper;
import e_learning.repositories.ResponseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
@AllArgsConstructor
public class ResponseService {

    private final ResponseRepository responseRepository;
    private final ResponseMapper responseMapper;
    private final PermissionService permissionService;

    public ResponseDto createResponse(ResponseDto responseDto) {
//        if (!permissionService.canManageResponse(responseDto.questionId())) {
//            throw new AccessDeniedException("User does not have permission to manage responses");
//        }

        Response response = responseMapper.toEntity(responseDto);
        response = responseRepository.save(response);
        return responseMapper.toDto(response);
    }

//    public ResponseDto updateResponse(Long id, ResponseDto responseDto) {
////        if (!permissionService.canManageResponse(responseDto.questionId())) {
////            throw new AccessDeniedException("User does not have permission to manage responses");
////        }
//
//        Response existingResponse = responseRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Response not found"));
//
//        existingResponse.setAnswer(responseDto.answer());
//
//        existingResponse = responseRepository.save(existingResponse);
//        return responseMapper.toDto(existingResponse);
//    }

    public void deleteResponse(Long id) {
        Response response = responseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Response not found"));
        responseRepository.delete(response);
    }

    public ResponseDto getResponse(Long id) {
        Response response = responseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Response not found"));
        return responseMapper.toDto(response);
    }
}
