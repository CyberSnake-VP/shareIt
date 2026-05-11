package example.request;

import example.exception.NotFoundException;
import example.item.Item;
import example.item.ItemRepository;
import example.item.dto.ItemMapper;
import example.item.dto.ItemResponse;
import example.request.dto.CreateRequestDto;
import example.request.dto.RequestMapper;
import example.request.dto.RequestResponse;
import example.request.dto.RequestResponseWithItems;
import example.user.User;
import example.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService{
    private final RequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    private static final String USER_NOT_FOUND_MESSAGE = "User not found";
    private static final String REQUEST_NOT_FOUND_MESSAGE= "Request not found";

    @Transactional
    @Override
    public RequestResponse create(Long requestorId, CreateRequestDto requestDto) {
        log.info("Create request started: requestorId={}", requestorId);

        getUserOrThrow(requestorId);
        Request request = RequestMapper.toRequest(requestDto, requestorId);
        Request savedRequest = repository.save(request);
        RequestResponse response = RequestMapper.toRequestResponseMapper(savedRequest);
        log.info("Create request completed: requestorId={},  requestId={}", requestorId, response.id());
        return response;
    }

    @Override
    public List<RequestResponseWithItems> getAll() {
        log.info("Get all requests started: ");

        List<Request> requests = repository.findAll();

        if(requests.isEmpty()) {
            log.info("Get all requests completed: ");
            return Collections.emptyList();
        }

        List<Long> requestIds = getRequestIds(requests);

        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);

        // id request : request
        Map<Long, List<Item>> itemMap = items.stream()
                .collect(Collectors.groupingBy(Item::getRequestId));

        List <RequestResponseWithItems> result = getRequestResponseWithItem(requests, itemMap);

        log.info("Get all requests completed: size={}", result.size());
        return result;
    }

    @Override
    public RequestResponseWithItems getById(Long id) {
        log.info("Get by id request started: id={}", id);

        Request request = getRequestOrThrow(id);

        List<Item> items = itemRepository.findAllByRequestId(request.getId());

        List<ItemResponse> itemsResponses = ItemMapper.toResponses(items);

        RequestResponseWithItems response = RequestMapper.toRequestResponseMapper(request, itemsResponses);

        log.info("Get by id request completed: id={}", id);
        return response;
    }

    @Override
    public List<RequestResponseWithItems> getByRequestorId(Long requestorId) {
        log.info("Get requests by requestor started: requestorId={}", requestorId);

        List<Request> requests = repository.findAllByRequestorId(requestorId);

        if (requests.isEmpty()) {
            log.info("Get requests by requestor completed: no requests found");
            return Collections.emptyList();
        }
        List<Long> requestIds = getRequestIds(requests);

        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);

        Map<Long, List<Item>> itemMap = items.stream().collect(Collectors.groupingBy(Item::getRequestId));

        List<RequestResponseWithItems> result = getRequestResponseWithItem(requests, itemMap);

        log.info("Get requests by requestor completed: ");
        return result;
    }


    private User getUserOrThrow(Long requestorId) {
        return userRepository.findById(requestorId).orElseThrow(()-> {
            log.warn("Get user by id is failed: not found, requestorId = {}", requestorId);
            return new NotFoundException(USER_NOT_FOUND_MESSAGE);
        });
    }
    private Request getRequestOrThrow(Long requestId) {
        return repository.findById(requestId).orElseThrow(() -> {
            log.warn("Get request by id is failed: not found, requestId={}", requestId);
            return new NotFoundException(REQUEST_NOT_FOUND_MESSAGE);
        });
    }

    private List<Long> getRequestIds(List<Request> requests) {
        return requests.stream().map(Request::getId).toList();
    }

    private List<RequestResponseWithItems> getRequestResponseWithItem(List<Request> requests, Map<Long, List<Item>> itemMap) {
        return requests.stream().map(request -> {
            List<Item> itemList = itemMap.getOrDefault(request.getId(), Collections.emptyList());
            List<ItemResponse> itemResponses = ItemMapper.toResponses(itemList);
            return RequestMapper.toRequestResponseMapper(request, itemResponses);
        }).toList();
    }
}
