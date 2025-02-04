
public class Main {

    private static ConfigFile config = new ConfigFile(
            100, 100, 1000, 100,
            90, 23456, 5, 69, 47, 50);

    public static ConfigFile getConfig() {
        return config;
    }

    public static void main(String[] args) {

        Player player1 = new Player("Player1");
        Map gameMap = new Map(11, 8);
        gameMap.createMap();

        // เริ่มเกม
        for (int turn = 1; turn <= config.max_turns(); turn++) {
            System.out.println("--- Turn " + turn + " ---");

            // อัปเดตจำนวนเทิร์นของผู้เล่น
            player1.incrementTurnCount();
            System.out.println(player1.getName()+"'s budget : " + player1.getIntBudget());

            // ตัวอย่างการใช้งาน
            MinionType warrior = new MinionType("Warrior", 0);
            player1.buyArea(5, 5, gameMap); // ซื้อพื้นที่
            Minion minionP1 = new Minion("P1Minion", warrior, 10, player1, gameMap);
            player1.spawnMinion(minionP1, 5, 5); // spawn Minion
            minionP1.shoot(1, 50); // ยิง

            // จบเทิร์น
            player1.done();

            // เพิ่มงบประมาณเมื่อเริ่มเทิร์นใหม่
            player1.addTurnBudget();

            // คำนวณดอกเบี้ย
            player1.calculateInterest();
        }
    }
}

//public class Main {
//    public static void main(String[] args) throws LexicalError, EvalError {
//        StatementParser p = new StatementParser(new ExprTokenizer("""
//                t = t + 1
//                m = 0
//                while (3 - m) {
//                  if (budget - 100) then {} else done
//                  opponentLoc = opponent
//                  if (opponentLoc / 10 - 1)
//                  then
//                    if (opponentLoc % 10 - 5) then move upleft
//                    else if (opponentLoc % 10 - 4) then move downleft
//                    else if (opponentLoc % 10 - 3) then move down
//                    else if (opponentLoc % 10 - 2) then move downright
//                    else if (opponentLoc % 10 - 1) then move upright
//                    else move up
//                  else if (opponentLoc)
//                  then
//                    if (opponentLoc % 10 - 5) then {
//                      cost = 10 ^ (nearby upleft % 100 + 1)
//                      if (budget - cost) then shoot upleft cost else {}
//                    }
//                    else if (opponentLoc % 10 - 4) then {
//                      cost = 10 ^ (nearby downleft % 100 + 1)
//                      if (budget - cost) then shoot downleft cost else {}
//                    }
//                    else if (opponentLoc % 10 - 3) then {
//                      cost = 10 ^ (nearby down % 100 + 1)
//                      if (budget - cost) then shoot down cost else {}
//                    }
//                    else if (opponentLoc % 10 - 2) then {
//                      cost = 10 ^ (nearby downright % 100 + 1)
//                      if (budget - cost) then shoot downright cost else {}
//                    }
//                    else if (opponentLoc % 10 - 1) then {
//                      cost = 10 ^ (nearby upright % 100 + 1)
//                      if (budget - cost) then shoot upright cost else {}
//                    }
//                    else {
//                      cost = 10 ^ (nearby up % 100 + 1)
//                      if (budget - cost) then shoot up cost else {}
//                    }
//                  else {
//                    try = 0
//                    while (3 - try) {
//                      success = 1
//                      dir = random % 6
//                      if ((dir - 4) * (nearby upleft % 10 + 1) ^ 2) then move upleft
//                      else if ((dir - 3) * (nearby downleft % 10 + 1) ^ 2) then move downleft
//                      else if ((dir - 2) * (nearby down % 10 + 1) ^ 2) then move down
//                      else if ((dir - 1) * (nearby downright % 10 + 1) ^ 2) then move downright
//                      else if (dir * (nearby upright % 10 + 1) ^ 2) then move upright
//                      else if ((nearby up % 10 + 1) ^ 2) then move up
//                      else success = 0
//                      if (success) then try = 3 else try = try + 1
//                    }
//                    m = m + 1
//                  }
//                }
//                """) );
//        try{
//            Strategy s = p.parse();
//        }
//        catch(Exception e){
//            System.out.println(e);
//        }
//
//
//        StatementParser q = new StatementParser(new ExprTokenizer("""
//                x = 20
//                if(x-20) then {done} else {move down}
//                move up
//                """) );
//            Strategy s = q.parse();
//        Minion m = new Minion("minion1","Warrior",5,"Player1");
//        System.out.println(m.getCol() + " " + m.getRow());
//        s.evaluator(m);
//        System.out.println(m.getCol() + " " + m.getRow());
//
//    }
//}
//
