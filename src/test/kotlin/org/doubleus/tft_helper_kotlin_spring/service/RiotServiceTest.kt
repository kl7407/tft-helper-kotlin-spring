package org.doubleus.tft_helper_kotlin_spring.service

import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File

@SpringBootTest
internal class RiotServiceTest
{
    private val filePath = "./src/main/assets"
    private val challengerPuuidFileName = "puuids_challenger.txt"
    private val grandmasterPuuidFileName = "puuids_grandmaster.txt"
    //private val masterPuuidFileName = "puuids_master.txt"
    private val matchIdFileName = "match_ids.txt"

    @Autowired
    private lateinit var riotService: RiotService

    @Test
    fun getChallengerLeagueListTest() {
        val challengerLeagueDto = riotService.getChallengerLeagueList()
        assert(challengerLeagueDto.tier == "CHALLENGER")
    }

    @Test
    fun getGrandmasterLeagueListTest() {
        val challengerLeagueDto = riotService.getGrandmasterLeagueList()
        assert(challengerLeagueDto.tier == "GRANDMASTER")
    }

    @Test
    fun getMasterLeagueListTest() {
        val challengerLeagueDto = riotService.getMasterLeagueList()
        assert(challengerLeagueDto.tier == "MASTER")
    }

    @Test
    fun getMatchInfoTest() {
        val matchDto = riotService.getMatchInfo("KR_5635478208")
        val gameInfo = matchDto.info
        assert(gameInfo.tft_game_type == "standard")
        assert(gameInfo.participants.size == 8)
    }

    @Test
    fun getTopTierPuuidsTest() {
        val topTierPuuids = riotService.getTopTierPuuids()
        val challengerNumber = 300
        val grandmasterNumber = 600
        assert(topTierPuuids.size == (challengerNumber + grandmasterNumber))
    }

    @Test
    fun makePuuidsFileTest() {
        riotService.makePuuidsFile()
        assert(File("${filePath}/${challengerPuuidFileName}").isFile)
        assert(File("${filePath}/${grandmasterPuuidFileName}").isFile)
    }

    @Test
    fun makeMatchIdsFileTest() {
        riotService.makeMatchIdsFile()
        assert(File("${filePath}/${matchIdFileName}").isFile)
    }
}