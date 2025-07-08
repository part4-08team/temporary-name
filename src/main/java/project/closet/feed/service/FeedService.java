package project.closet.feed.service;

import project.closet.dto.request.FeedCreateRequest;
import project.closet.dto.response.FeedDto;

public interface FeedService {

    FeedDto createFeed(FeedCreateRequest feedCreateRequest);

}
