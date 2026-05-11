package example.request.dto;

import example.item.dto.ItemResponse;
import example.request.Request;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    public static RequestResponseWithItems toRequestResponseMapper(Request request, List<ItemResponse> itemList) {
        return new RequestResponseWithItems(
                request.getId(),
                request.getDescription(),
                request.getRequestorId(),
                request.getCreated(),
                itemList
        );
    }

    public static Request toRequest(CreateRequestDto requestDto, Long requestorId) {
        Request request = new Request();
        request.setDescription(requestDto.description());
        request.setRequestorId(requestorId);
        return request;
    }

    public static RequestResponse toRequestResponseMapper(Request request) {
        return new RequestResponse(
                request.getId(),
                request.getDescription(),
                request.getRequestorId(),
                request.getCreated()
        );
    }

}
