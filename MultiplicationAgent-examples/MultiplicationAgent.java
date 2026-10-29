import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 一个简单的"九九乘法表 Agent"。
 * 结构：感知(perceive) -> 决策/行动(act) -> 记忆(memory)
 */
public class MultiplicationAgent {

    private static final int MAX = 9;
    private static final Pattern EXPR = Pattern.compile("(\\d+)\\s*[×xX*乘]\\s*(\\d+)");
    private static final Pattern NUM = Pattern.compile("\\d+");

    /** 记忆：回答过的历史 */
    private final List<String> memory = new ArrayList<>();
    /** 能力：乘法 */
    private final MultiplicationSkill skill = new MultiplicationSkill();

    // ---------------- 感知：把用户输入解析成两个操作数 ----------------
    private int[] perceive(String input) {
        if (input == null)
            return null;

        Matcher m = EXPR.matcher(input);
        if (m.find()) {
            return new int[] { Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)) };
        }

        // 退化处理："3 4" 这种只给了两个数字的输入
        Matcher nums = NUM.matcher(input);
        List<Integer> list = new ArrayList<>();
        while (nums.find())
            list.add(Integer.parseInt(nums.group()));
        if (list.size() >= 2)
            return new int[] { list.get(0), list.get(1) };

        return null;
    }

    // ---------------- 决策 + 行动：查表并回答 ----------------
    public String act(int a, int b) {
        int result = skill.multiply(a, b); // ← 查表/计算交给 Skill

        String answer = a + " × " + b + " = " + result
                + (skill.isWithinTable(a, b) ? "（查九九乘法表）" : "（超出九九表范围，直接计算）");

        memory.add(a + "x" + b + "=" + result);
        return answer;
    }

    /** 一步完整的 agent 循环 */
    public String step(String input) {
        int[] operands = perceive(input);
        if (operands == null)
            return "我没看懂，请这样问我：3*4 或 7 乘 8";
        try {
            return act(operands[0], operands[1]);
        } catch (ArithmeticException e) {
            return "算不了：" + e.getMessage();
        }
    }

    public List<String> getMemory() {
        return new ArrayList<>(memory);
    }

    // ---------------- 打印标准九九乘法表 ----------------
    public static void printTable() {
        for (int i = 1; i <= MAX; i++) {
            for (int j = 1; j <= i; j++) {
                System.out.printf("%d×%d=%-3d", j, i, i * j);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        MultiplicationAgent agent = new MultiplicationAgent();

        System.out.println("=== 九九乘法表 Agent ===");
        System.out.println("输入 3*4 / 7乘8 这样的算式；输入 table 打印九九表；输入 exit 退出");

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("你> ");
                if (!sc.hasNextLine())
                    break;

                String line = sc.nextLine().trim();
                if (line.isEmpty())
                    continue;

                if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
                    System.out.println("Agent> 再见！本次共回答 " + agent.getMemory().size() + " 个问题。");
                    break;
                }
                if (line.equalsIgnoreCase("table")) {
                    printTable();
                    continue;
                }

                System.out.println("Agent> " + agent.step(line));
            }
        }
    }
}