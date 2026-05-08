package example.request;

import example.request.dto.CreateRequestDto;
import example.request.dto.RequestResponse;
import example.request.dto.RequestResponseWithItems;

import java.util.List;

public interface RequestService {
    RequestResponse create(Long requestorId, CreateRequestDto requestDto);
    List<RequestResponseWithItems> getAll();
    RequestResponseWithItems getById(Long id);
    List<RequestResponseWithItems> getByRequestorId(Long requestorId);
}
