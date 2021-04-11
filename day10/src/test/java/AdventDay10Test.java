import static com.mscg.ChipFactoryMoveRuleBuilder.MoveRule;
import static com.mscg.ChipFactorySetRuleBuilder.SetRule;
import static com.mscg.ChipFactoryTargetBuilder.Target;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.mscg.ChipFactory;
import com.mscg.ChipFactory.TargetType;
import com.mscg.ChipFactoryMoveRuleBuilder;
import com.mscg.ChipFactoryTargetBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay10Test {

    @Test
    public void testParse() throws Exception {
        try (BufferedReader in = readInput()) {
            var chipFactory = ChipFactory.parseInput(in);

            Assertions.assertEquals(List.of( //
                    SetRule(2, 5), //
                    MoveRule(2, Target(TargetType.BOT, 1), Target(TargetType.BOT, 0)), //
                    SetRule(1, 3), //
                    MoveRule(1, Target(TargetType.OUTPUT, 1), Target(TargetType.BOT, 0)), //
                    MoveRule(0, Target(TargetType.OUTPUT, 2), Target(TargetType.OUTPUT, 0)), //
                    SetRule(2, 2) //
                    ), chipFactory.getRules());
        }
    }

    @Test
    public void testExecuteComplete() throws Exception {
        try (BufferedReader in = readInput()) {
            var chipFactory = ChipFactory.parseInput(in);

            chipFactory.execute(__ -> true);

            Assertions.assertEquals(Map.of( //
                    0, 5, //
                    1, 2, //
                    2, 3 //
                    ), chipFactory.getOutputs());
        }
    }

    @Test
    public void testExecutePartial() throws Exception {
        try (BufferedReader in = readInput()) {
            var chipFactory = ChipFactory.parseInput(in);

            int index = chipFactory.execute(bot -> !(bot.high() == 5 && bot.low() == 2));

            Assertions.assertEquals(2, index);
        }
    }
    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
