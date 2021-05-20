package com.game.service;

import com.game.entity.Player;
import com.game.entity.PlayerSearchCriteria;
import com.game.repository.PlayerCriteriaRepository;
import com.game.repository.PlayersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Service
public class PlayersService {

    private final PlayersRepository playersRepository;
    private final PlayerCriteriaRepository playerCriteriaRepository;

    @Autowired
    public PlayersService(PlayersRepository playersRepository, PlayerCriteriaRepository playerCriteriaRepository) {
        this.playersRepository = playersRepository;
        this.playerCriteriaRepository = playerCriteriaRepository;
    }

    public List<Player> getAllPlayers(PlayerSearchCriteria playerSearchCriteria) {

        Page<Player> playerPage = playerCriteriaRepository.findAllWithFilters(playerSearchCriteria);

        return playerPage.getContent();
    }

    public Integer getPlayersCount(PlayerSearchCriteria playerSearchCriteria) {

        return playerCriteriaRepository.countAllWithFilters(playerSearchCriteria);
    }

    public Player save(Player player) {

        int level = (int) ((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100);
        int untilNextLevel = 50 * (level + 1) * (level + 2) - player.getExperience();

        player.setLevel(level);
        player.setUntilNextLevel(untilNextLevel);

        return playersRepository.save(player);
    }

    public boolean createPlayer(Player player) {
        return player.getName() != null && dataCheck(player, "name")
                && player.getTitle() != null && dataCheck(player, "title")
                && player.getRace() != null
                && player.getProfession() != null
                && player.getBanned() != null
                && player.getBirthday() != null && dataCheck(player, "date")
                && player.getExperience() != null && dataCheck(player, "experience");
    }

    public boolean idCheck(long id) {

        if (id <= 0) {
            return false;
        }

        try {
            String checkId = id + "";
            Long.parseLong(checkId);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public boolean dataCheck(Player player, String checkFor) {

        switch (checkFor) {
            case "name":
                return !player.getName().isEmpty() && player.getName().length() <= 12;
            case "title":
                return player.getTitle().length() <= 30;
            case "date":
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    long startDate = sdf.parse("2000-01-01").getTime();
                    long endDate = sdf.parse("3000-12-31").getTime();

                    return player.getBirthday().getTime() >= 0 && player.getBirthday().getTime() >= startDate && player.getBirthday().getTime() <= endDate;
                } catch (ParseException e) {
                    return false;
                }
            case "experience":
                return player.getExperience() >= 0 && player.getExperience() <= 10000000;
        }
        return false;
    }

    public Optional<Player> getPlayer(long id) {
        return playersRepository.findById(id);
    }

    public void deletePLayer(long id) {
        playersRepository.deleteById(id);
    }

    public Player updatePlayer(Player player, long id) {

        if (getPlayer(id).isPresent()) {
            Player playerToUpdate = getPlayer(id).get();

            if (player.getName() != null) {
                if (dataCheck(player, "name")) {
                    playerToUpdate.setName(player.getName());
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            }

            if (player.getTitle() != null) {
                if (dataCheck(player, "title")) {
                    playerToUpdate.setTitle(player.getTitle());
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            }

            if (player.getRace() != null) {
                playerToUpdate.setRace(player.getRace());
            }

            if (player.getProfession() != null) {
                playerToUpdate.setProfession(player.getProfession());
            }

            if (player.getBirthday() != null) {
                if (dataCheck(player, "date")) {
                    playerToUpdate.setBirthday(player.getBirthday());
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            }

            if (player.getBanned() != null) {
                playerToUpdate.setBanned(player.getBanned());
            }

            if (player.getExperience() != null) {
                if (dataCheck(player, "experience")) {
                    playerToUpdate.setExperience(player.getExperience());
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            }
            return save(playerToUpdate);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}

