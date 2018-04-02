package io.block16.ethlistener.handler;

import io.block16.ethlistener.domain.WatchRequest;
import io.block16.ethlistener.service.ListenerService;
import io.block16.response.exceptions.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import java.util.Set;

@RestController
public class WatchController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final ListenerService listenerService;

    @Autowired
    public WatchController(final ListenerService listenerService) {
        this.listenerService = listenerService;
    }

    /**
     * Begin watching an address for incoming transactions
     */
    @PostMapping(path = "/v1/watch")
    public void watchAddress(@RequestBody WatchRequest watchRequest, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new BadRequestException();
        }
        this.listenerService.watchAddress(watchRequest);
    }

    @GetMapping(path = "/v1/watch")
    public Set<Object> getWatching() {
        return this.listenerService.getWatching();
    }

}
