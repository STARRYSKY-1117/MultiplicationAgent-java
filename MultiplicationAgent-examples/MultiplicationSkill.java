/**
 * 九九乘法 Skill：只负责“算”，不管输入解析、不管记忆、不管打印。
 */
public class MultiplicationSkill {

    private static final int MAX = 9;
    private final int[][] table = new int[MAX + 1][MAX + 1];

    public MultiplicationSkill() {
        for (int a = 1; a <= MAX; a++) {
            for (int b = 1; b <= MAX; b++) {
                table[a][b] = a * b;
            }
        }
    }

    /** 是否落在九九表范围内，供上层决定文案 */
    public boolean isWithinTable(int a, int b) {
        return a >= 1 && a <= MAX && b >= 1 && b <= MAX;
    }

    /** 乘法：表内查表，表外直算；溢出抛 ArithmeticException */
    public int multiply(int a, int b) {
        long r = (long) a * b;
        if (r > Integer.MAX_VALUE || r < Integer.MIN_VALUE) {
            throw new ArithmeticException("结果超出 int 范围");
        }
        return isWithinTable(a, b) ? table[a][b] : (int) r;
    }
}