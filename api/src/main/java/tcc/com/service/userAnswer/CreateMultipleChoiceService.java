package tcc.com.service.userAnswer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tcc.com.controller.request.userAnswer.AnswerRequest;
import tcc.com.controller.response.userAnswer.AnswerResponse;
import tcc.com.domain.exercise.Exercise;
import tcc.com.domain.exerciseOption.ExerciseOption;
import tcc.com.domain.level.Level;
import tcc.com.domain.ranking.Ranking;
import tcc.com.domain.user.User;
import tcc.com.domain.userAnswer.UserAnswer;
import tcc.com.domain.userCourseProgress.UserCourseProgress;
import tcc.com.mapper.UserAnswerMapper;
import tcc.com.mapper.UserCourseProgressMapper;
import tcc.com.repository.*;
import tcc.com.security.AuthenticatedUserService;

@Service
public class CreateMultipleChoiceService {

    @Autowired
    private ExerciseOptionRepository exerciseOptionRepository;

    @Autowired
    private UserAnswerRepository userAnswerRepository;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserCourseProgressRepository userCourseProgressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private RankingRepository rankingRepository;

    private static final int MULTIPLE_CHOICE_XP = 20;
    private static final int WRONG_MULTIPLE_CHOICE_XP = 5;

    private static final int ADVERGAME_XP = 30;
    private static final int BOSS_XP = 50;

    private static final int WRONG_COINS = 3;

    public ResponseEntity<AnswerResponse> create(Long exerciseId, AnswerRequest request) {
        ExerciseOption exerciseOption = exerciseOptionRepository.findByExerciseIdAndCorrectTrue(exerciseId);

        User user = authenticatedUserService.get();
        Ranking ranking = rankingRepository.findByUser(user);

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Esse exercício não existe."));

        boolean isCorrect = exerciseOption.getContent().equals(request.getAnswer());
        request.setCorrect(isCorrect);

        UserAnswer userAnswer = userAnswerRepository.findByUserAndExercise(user, exercise);

        if(userAnswer == null) {
            userAnswer = UserAnswerMapper.toEntity(request, exercise, user);
        } else {
            userAnswer.setAnswer(request.getAnswer());
            userAnswer.setCorrect(request.isCorrect());
            userAnswer.setLesson(exercise.getLesson());
        }

        if(isCorrect) {
            if(!userAnswer.isAlreadyAnswered()) {
                switch (exercise.getLesson().getExerciseCategory().getName()) {
                    case ADVERGAME:
                        ranking.setPoints(ranking.getPoints() + ADVERGAME_XP);
                        user.setCurrentXp(user.getCurrentXp() + ADVERGAME_XP);
                        user.setCoins(user.getCoins() + (5 + (ADVERGAME_XP/10)));
                        break;
                    case BOSS:
                        ranking.setPoints(ranking.getPoints() + BOSS_XP);
                        user.setCurrentXp(user.getCurrentXp() + BOSS_XP);
                        user.setCoins(user.getCoins() + (5 + (BOSS_XP/10)));
                        break;
                    default:
                        ranking.setPoints(ranking.getPoints() + MULTIPLE_CHOICE_XP);
                        user.setCurrentXp(user.getCurrentXp() + MULTIPLE_CHOICE_XP);
                        user.setCoins(user.getCoins() + (5 + (MULTIPLE_CHOICE_XP/10)));
                        break;
                }

                rankingRepository.save(ranking);
            }
            UserCourseProgress userCourseProgress = userCourseProgressRepository.findByUserAndArea(user, exercise.getLesson().getChapter().getArea());
            if (userCourseProgress == null) {
                userCourseProgress = UserCourseProgressMapper.toEntity(user, exercise.getLesson().getChapter().getArea(), exercise.getLesson());
            } else {
                if (exercise.getLesson().getId() > userCourseProgress.getLastUnlockedLesson().getId()) {
                    userCourseProgress.setLastUnlockedLesson(exercise.getLesson());
                    userCourseProgress.setCompletedExercises(userCourseProgress.getCompletedExercises() + 1);
                }
            }
            userCourseProgressRepository.save(userCourseProgress);
        }else {
            if(!userAnswer.isAlreadyAnswered()) {
                user.setCurrentXp(user.getCurrentXp() + WRONG_MULTIPLE_CHOICE_XP);
                user.setCoins(user.getCoins() + WRONG_COINS);
            }
        }

        userRepository.save(user);

        Level currentLevel = user.getLevel();
        Level nextLevel = levelRepository.findByLevelNumber(currentLevel.getLevelNumber() + 1);

        if(nextLevel != null) {
            if(user.getCurrentXp() >= nextLevel.getRequiredXp()) {
                user.setLevel(nextLevel);
            }
        }

        if(!isCorrect) {
            userAnswer.setAlreadyAnswered(false);
            userAnswerRepository.save(userAnswer);
            return ResponseEntity.ok(new AnswerResponse(false, "Resposta incorreta, tente novamente!"));
        }else {
            userAnswer.setAlreadyAnswered(true);
            userAnswerRepository.save(userAnswer);
            return ResponseEntity.ok(new AnswerResponse(true, "Resposta correta!"));
        }
    }
}
