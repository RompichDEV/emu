package com.eu.habbo.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class EnigmaBot {

    private final List<Enigma> enigmas;
    private final Random random;

    public EnigmaBot() {
        enigmas = new ArrayList<>();
        random = new Random();

        // Ajoutez vos énigmes ici
        enigmas.add(new Enigma("Je peux voler sans avoir d'ailes. Qui suis-je ?", "le temps"));
        enigmas.add(new Enigma("Plus on en donne, plus on en a. Qu'est-ce que c'est ?", "un conseil"));
    }

    public void askEnigma() {
        // Choisissez une énigme au hasard
        Enigma currentEnigma = getRandomEnigma();

        // Posez la question
        System.out.println("Énigme: " + currentEnigma.getQuestion());

        // Attendez la réponse de l'utilisateur
        Scanner scanner = new Scanner(System.in);
        String userAnswer = scanner.nextLine().toLowerCase();

        // Vérifiez la réponse
        if (userAnswer.equals(currentEnigma.getAnswer())) {
            System.out.println("Bravo, vous avez trouvé la bonne réponse !");
        } else {
            System.out.println("Désolé, la réponse était incorrecte. La réponse correcte était : " + currentEnigma.getAnswer());
        }
    }

    private Enigma getRandomEnigma() {
        int randomIndex = random.nextInt(enigmas.size());
        return enigmas.get(randomIndex);
    }

    public static void main(String[] args) {
        EnigmaBot bot = new EnigmaBot();

        // Posez une énigme
        bot.askEnigma();
    }
}

class Enigma {
    private final String question;
    private final String answer;

    public Enigma(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
