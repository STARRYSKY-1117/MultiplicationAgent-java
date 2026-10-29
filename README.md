# MultiplicationAgent-java
# 九九乘法表 Agent + Skill

一个用 Java 写的极简 Agent 示例：把"能力（Skill）"和"对话外壳（Agent）"分开。

- **Skill**：只负责算乘法（查九九表 / 表外直算 / 溢出保护），可单独测试、复用。
- **Agent**：负责解析用户输入、调用 Skill、记录历史、打印结果。

## 文件结构

```
MultiplicationAgent.java   ← 有 main，运行这个
MultiplicationSkill.java   ← 被 Agent 调用，不单独运行
```

两个文件放在同一目录、同一包下。

## 运行

```bash
javac MultiplicationAgent.java MultiplicationSkill.java
java MultiplicationAgent
```

IDE 里直接运行 `MultiplicationAgent.java` 的 `main` 方法。

## 使用示例

```
=== 九九乘法表 Agent ===
输入 3*4 / 7乘8 这样的算式；输入 table 打印九九表；输入 exit 退出
你> 3*4
Agent> 3 × 4 = 12（查九九乘法表）
你> 7乘8
Agent> 7 × 8 = 56（查九九乘法表）
你> 12*12
Agent> 12 × 12 = 144（超出九九表范围，直接计算）
你> table
1×1=1
1×2=2  2×2=4
...
你> exit
Agent> 再见！本次共回答 3 个问题。
```

## 支持的输入

| 输入 | 说明 |
|---|---|
| `3*4`、`3x4`、`3×4`、`3乘4` | 标准算式 |
| `3 4` | 只给两个数字也能识别 |
| `table` | 打印标准九九乘法表 |
| `exit` / `quit` | 退出 |

## 职责划分

| 位置 | 负责 |
|---|---|
| `MultiplicationSkill.multiply(a, b)` | 算乘法，表内查表，表外直算，防溢出 |
| `MultiplicationSkill.isWithinTable(a, b)` | 判断是否在 1~9 范围内，供上层选文案 |
| `MultiplicationAgent.perceive(input)` | 正则解析出两个操作数 |
| `MultiplicationAgent.act(a, b)` | 调用 Skill + 拼文案 + 记历史 |
| `MultiplicationAgent.step(input)` | 一步完整的 agent 循环 |
| `MultiplicationAgent.printTable()` | 打印标准九九表 |

## 怎么单独测 Skill

Skill 没有 `main`，用 JUnit：

```java
class MultiplicationSkillTest {
    MultiplicationSkill s = new MultiplicationSkill();

    @Test void tableHit()  { assertEquals(81, s.multiply(9, 9)); }
    @Test void tableMiss() { assertEquals(144, s.multiply(12, 12)); }
    @Test void overflow()  { assertThrows(ArithmeticException.class,
                                           () -> s.multiply(2_000_000_000, 2)); }
}
```

## 设计取舍

- **没上接口/注册表**：只有一个 Skill，直接 `new` 字段调用最简单。
- **Skill 无状态**：`memory` 留在 Agent，Skill 只有只读的 `table`，方便并发复用。
- **溢出保护在 Skill**：`(long) a * b` 判断后再返回 `int`，防止静默算错。
- **行为与原版一致**：表外输入仍直接计算，只是多了溢出兜底。

## 何时升级

出现第二个 Skill、或要接 LLM function calling / MCP 时，再把上一版的
`Skill<I,O>` 接口 + `SkillRegistry` + `ToolSpec` 加回来。现在加是过度设计。
