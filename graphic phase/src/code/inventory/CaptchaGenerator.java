package code.inventory;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Random;

public class CaptchaGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CAPTCHA_LENGTH = 6;

    public static String generateCaptchaText() {
        StringBuilder captchaText = new StringBuilder(CAPTCHA_LENGTH);
        Random random = new Random();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captchaText.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return captchaText.toString();
    }

    public static Canvas generateCaptchaImage(String captchaText) {
        int width = 200;
        int height = 80;
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw background
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, width, height);

        // Draw CAPTCHA text
        gc.setFont(Font.font("Verdana", 40));
        Random random = new Random();
        for (int i = 0; i < captchaText.length(); i++) {
            gc.setFill(new Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), 1.0));
            double x = 20 + i * 30 + random.nextInt(10);
            double y = 40 + random.nextInt(20);
            gc.save();
            gc.translate(x, y);
            gc.rotate(random.nextInt(30) - 15);
            gc.fillText(String.valueOf(captchaText.charAt(i)), 0, 0);
            gc.restore();
        }

        // Draw some noise
        for (int i = 0; i < 20; i++) {
            gc.setStroke(new Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), 1.0));
            gc.strokeLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
        }

        return canvas;
    }
}
