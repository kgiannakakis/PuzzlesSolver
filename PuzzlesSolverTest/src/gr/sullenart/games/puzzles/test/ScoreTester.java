package gr.sullenart.games.puzzles.test;

import gr.sullenart.games.puzzles.PuzzlesCollection;
import gr.sullenart.games.puzzles.ScoresActivity;
import gr.sullenart.scores.Score;
import gr.sullenart.scores.ScoresDbAdapter;
import gr.sullenart.scores.ScoresManager;
import gr.sullenart.scores.ScoresManagerFactory;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

public class ScoreTester extends android.test.ActivityInstrumentationTestCase2<PuzzlesCollection>{

	private Activity activity;
	
	private ScoresManager scoresManager;

	private String [] gameGroups = ScoresActivity.gameGroups;
	
	private String [] gameNames = {"game 1", "game 2", "game 3", "game 4"};
	
	public ScoreTester() {
		super("gr.sullenart.games.puzzles.PuzzlesCollection", PuzzlesCollection.class);
	}
	
	public ScoreTester(String pkg, Class<PuzzlesCollection> activityClass) {
		super(pkg, activityClass);
	}

	private Context getContext() {
		return ((PuzzlesCollection) activity).getApplicationContext();
	}
	
	private void initialize() {
        activity = this.getActivity();  
        
        ScoresDbAdapter scoresDbAdapter = new ScoresDbAdapter(getContext());
        scoresDbAdapter.open();
        scoresDbAdapter.deleteAllScores();
        
        scoresDbAdapter.close();
        
        scoresManager = ScoresManagerFactory.getScoresManager(getContext());
	}
	
	@Override
	protected void setUp() throws Exception {
        super.setUp();
    }

	@SmallTest
	public void testPreconditions() {
		initialize();
	
		assertNotNull(scoresManager);
		for(String game: gameNames) {
			assertEquals(scoresManager.getScores(game).size(), 0);
		}
	}
	
	@SmallTest
	public void testGameGroups() {
		initialize();
		String [] players = { "Player 1", "Player 3", "Player 2"};
		int [] scoreTimes = { 20, 30, 25 };
		
		String [] playersRank  = { "Player 1", "Player 2", "Player 3"};
		int [] scoreTimesRank = { 20, 25, 30 };
		
		for(String gameGroup: gameGroups) {
			for(String gameName: gameNames) {
				for(int i=0; i<players.length; i++) {
					Log.d("Test", gameName + " " + gameGroup + " " +players[i] + ", " + scoreTimes[i]);
					assertTrue(scoresManager.addScore(gameGroup + gameName, gameGroup, players[i], scoreTimes[i]));					
				}
			}
		}
		
		for(String gameGroup: gameGroups) {
			List<Score> scores = scoresManager.getScoresByGroup(gameGroup);
			assertEquals(scores.size(), gameNames.length*players.length);
			for(String gameName: gameNames) {
				List<Score> gameScores = new ArrayList<Score>();
				for(Score score: scores) {
					if (score.getGame().equals(gameGroup + gameName)) {
						gameScores.add(score);
					}
				}
				assertEquals(gameScores.size(), players.length);
				for(int i=0; i<players.length; i++) {
					assertEquals(gameScores.get(i).getPlayer(), playersRank[i]);
					assertEquals(gameScores.get(i).getScore(), scoreTimesRank[i]);
				}
			}
		}
		
		scoresManager = ScoresManagerFactory.getScoresManager(getContext());

		for(String gameGroup: gameGroups) {
			List<Score> scores = scoresManager.getScoresByGroup(gameGroup);
			assertEquals(scores.size(), gameNames.length*players.length);
			for(String gameName: gameNames) {
				List<Score> gameScores = new ArrayList<Score>();
				for(Score score: scores) {
					if (score.getGame().equals(gameGroup+gameName)) {
						gameScores.add(score);
					}
				}
				assertEquals(gameScores.size(), players.length);
				for(int i=0; i<players.length; i++) {
					assertEquals(gameScores.get(i).getPlayer(), playersRank[i]);
					assertEquals(gameScores.get(i).getScore(), scoreTimesRank[i]);
				}
			}
		}	
	}
	
	@SmallTest
	public void testHighScores() {
		initialize();
		
		String game = "Test game";
		String gameGroup = "group";
		scoresManager.setHigherIsBetter(true);
		scoresManager.setHighScoresPerGame(3);

		assertTrue(scoresManager.addScore(game, gameGroup, "Player 1", 100));
		assertEquals(scoresManager.getScores(game).size(), 1);
		assertTrue(scoresManager.addScore(game, gameGroup, "Player 1", 101));
		assertEquals(scoresManager.getScores(game).size(), 2);
		assertTrue(scoresManager.isHighScore(game, 99));
		assertTrue(scoresManager.addScore(game, gameGroup, "Player 1", 102));
		assertEquals(scoresManager.getScores(game).size(), 3);
		assertTrue(scoresManager.addScore(game, gameGroup, "Player 1", 103));
		assertFalse(scoresManager.addScore(game, gameGroup, "Player 1", 99));
		List<Score> scores = scoresManager.getScores(game);
		assertEquals(scores.size(), 3);
		assertEquals(scores.get(0).getScore(), 103);
		assertEquals(scores.get(1).getScore(), 102);
		assertEquals(scores.get(2).getScore(), 101);
		assertTrue(scoresManager.isHighScore(game, 104));
		assertTrue(scoresManager.isHighScore(game, 102));
		assertFalse(scoresManager.isHighScore(game, 101));

		initialize();

		scoresManager.setHigherIsBetter(false);

		assertTrue(scoresManager.addScore(game, gameGroup, "Player 1", 100));
		assertTrue(scoresManager.addScore(game, gameGroup, "Player 1", 101));
		assertTrue(scoresManager.isHighScore(game, 99));
		assertTrue(scoresManager.addScore(game, gameGroup, "Player 1", 102));
		assertTrue(scoresManager.addScore(game, gameGroup, "Player 1", 103));
		assertFalse(scoresManager.addScore(game, gameGroup, "Player 1", 104));

		scores = scoresManager.getScores(game);
		assertEquals(scores.size(), 3);
		assertEquals(scores.get(0).getScore(), 100);
		assertEquals(scores.get(1).getScore(), 101);
		assertEquals(scores.get(2).getScore(), 102);
		assertTrue(scoresManager.isHighScore(game, 99));
		assertTrue(scoresManager.isHighScore(game, 101));
		assertFalse(scoresManager.isHighScore(game, 102));
	}	
}
