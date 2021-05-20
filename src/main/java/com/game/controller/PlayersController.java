package com.game.controller;

import com.game.entity.*;
import com.game.service.PlayersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/rest/players")
public class PlayersController {

    private final PlayersService playersService;

    @Autowired
    public PlayersController(PlayersService playersService) {
        this.playersService = playersService;
    }

    @GetMapping
    public List<Player> getPlayersList(PlayerSearchCriteria playerSearchCriteria) {
        return playersService.getAllPlayers(playerSearchCriteria);
    }

    @GetMapping("/count")
    public Integer getPlayersCount(PlayerSearchCriteria playerSearchCriteria) {
        return playersService.getPlayersCount(playerSearchCriteria);
    }

    @PostMapping
    @ResponseBody
    public Player createPlayer(@RequestBody Player player) {
        if (playersService.createPlayer(player)) {
            return playersService.save(player);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public Player getPlayer(@PathVariable("id") long id) {
        if (!playersService.idCheck(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (playersService.getPlayer(id).isPresent()) {
            return playersService.getPlayer(id).get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public void deletePlayer(@PathVariable("id") long id) {

        if (!playersService.idCheck(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (playersService.getPlayer(id).isPresent()) {
            playersService.deletePLayer(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}")
    @ResponseBody
    public Player updatePlayer(@RequestBody Player player, @PathVariable("id") long id) {

        if (playersService.idCheck(id)) {
            return playersService.updatePlayer(player, id);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
