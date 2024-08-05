package menus.SignupClasses;

import java.util.*;

import inventory.*;
import menus.Menu;

public class Captcha extends Menu {
    private static final Map<String, String[]> asciiMap = new HashMap<>();
    private static final Random random = new Random();

    static {
        asciiMap.put("0", new String[] {
                " ***** ",
                "*     *",
                "*     *",
                "*     *",
                " ***** "
        });
        asciiMap.put("1", new String[] {
                "   **  ",
                "  ***  ",
                "   **  ",
                "   **  ",
                " ******"
        });
        asciiMap.put("2", new String[] {
                " ***** ",
                "*     *",
                "    ** ",
                "  **   ",
                " ******"
        });
        asciiMap.put("3", new String[] {
                " ***** ",
                "*     *",
                "   *** ",
                "*     *",
                " ***** "
        });
        asciiMap.put("4", new String[] {
                "*    * ",
                "*    * ",
                "*******",
                "     * ",
                "     * "
        });
        asciiMap.put("5", new String[] {
                "*******",
                "*      ",
                "*******",
                "      *",
                "*******"
        });
        asciiMap.put("6", new String[] {
                " ***** ",
                "*      ",
                "*******",
                "*     *",
                " ***** "
        });
        asciiMap.put("7", new String[] {
                "*******",
                "     * ",
                "    *  ",
                "   *   ",
                "  *    "
        });
        asciiMap.put("8", new String[] {
                " ***** ",
                "*     *",
                " ***** ",
                "*     *",
                " ***** "
        });
        asciiMap.put("9", new String[] {
                " ***** ",
                "*     *",
                "*******",
                "      *",
                " ***** "
        });
        asciiMap.put("n0", new String[] {
                "   {}  ",
                " # 45  ",
                " #  ## ",
                "   #   ",
                "       "
        });
        asciiMap.put("n1", new String[] {
                "     # ",
                "*******",
                "  #    ",
                "*******",
                "  ###  "
        });
        asciiMap.put("n2", new String[] {
                " * *   ",
                "*     *",
                " **  * ",
                "  *    ",
                "  *    "
        });
        asciiMap.put("n3", new String[] {
                " 3     ",
                "  +  v ",
                "   j f ",
                "  ()   ",
                "     _ "
        });
    }

    public static String generateAsciiArt(String input) {
        StringBuilder[] lines = new StringBuilder[5];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = new StringBuilder();
        }

        for (char c : input.toCharArray()) {
            int randomInt = random.nextInt(4);
            String[] art = asciiMap.getOrDefault(String.valueOf(c),
                    asciiMap.get("n" + (randomInt)));
            String[] noise = asciiMap.get("n" + (randomInt));
            for (int i = 0; i < lines.length; i++) {
                lines[i].append(art[i]).append(" ");
            }
            for (int i = 0; i < lines.length; i++) {
                lines[i].append(noise[i]).append(" ");
            }
        }

        StringBuilder result = new StringBuilder();
        for (StringBuilder line : lines) {
            result.append(line).append("\n");
        }

        return result.toString();
    }

    public Captcha(DataManager dataManager) {
        super(dataManager);
    }

    // this is how to show the ascii art. input must be a contiguous sequence of
    // numbers with the type of String
    // example for input is: "8856"
    public void showCaptcha(String input) {
        String asciiArt = generateAsciiArt(input);
        System.out.println(asciiArt);
    }

    public String randomFourDigitNumber() {
        String firstNumber = String.valueOf(random.nextInt(10));
        String secondNumber = String.valueOf(random.nextInt(10));
        String thirdNumber = String.valueOf(random.nextInt(10));
        String forthNumber = String.valueOf(random.nextInt(10));
        return firstNumber + secondNumber + thirdNumber + forthNumber;
    }

    public SignupState run() {
        String captchaString = randomFourDigitNumber();
        showCaptcha(captchaString);

        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("Enter the four digit number you see above: ");

        while(!(input = scanner.nextLine()).equals("end")) {
            if(input.equals("current menu name")) {
                System.out.println("Signup menu");
                continue;
            }

            if(!input.equals(captchaString) && !input.equals("To Register menu") && !input.equals("To Login menu")) {
                System.out.println("Wrong Input.");
                continue;
            }
            else if(input.equals(captchaString)){
                dataManager.addPlayer(dataManager.getCurrentPlayer());
                ArrayList<Card> cards = dataManager.getAllCards();
                Collections.shuffle(cards, random);
                for(int i = 0; i < 20; i++) {
                    dataManager.addPlayerCard(dataManager.getCurrentPlayer().getInfo("username"), cards.get(i));
                    dataManager.getCurrentPlayer().addCard(cards.get(i));
                }
                System.out.println("User created successfully.");
                return SignupState.MAIN_MENU;
            }

            if(input.equals("To Login menu")) {
                return SignupState.LOGIN;
            }

            return SignupState.USER_REGISTER;//upon writing To Register menu
        }

        return SignupState.EXIT;
    }
}