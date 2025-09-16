/*
 * AIChatbotFull.java
 *
 * Single-file Java chatbot with:
 *  - Simple NLP preprocessing (tokenize, stopwords, punctuation removal)
 *  - TF–IDF + cosine similarity FAQ matching
 *  - Rule-based replies (greeting, thanks, bye)
 *  - GUI (Swing) with menu: Load FAQs, Save FAQs, Add FAQ
 *  - FAQ persistence using "faq.txt" (format: question|answer per line)
 *
 * Usage:
 *   javac AIChatbotFull.java
 *   java AIChatbotFull
 *
 * Author: Generated for Ankit
 */

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class AIChatbotFull {
    public static void main(String[] args) {
        ChatbotEngine engine = new ChatbotEngine();
        engine.loadDefaultFaqs();           // load built-in FAQs
        engine.loadFaqFileIfExists("faq.txt"); // optionally load persisted faqs if file exists

        SwingUtilities.invokeLater(() -> {
            ChatGuiWithMenu gui = new ChatGuiWithMenu(engine);
            gui.show();
        });
    }
}

/* -----------------------------
   Chat GUI with Menu (Swing)
   ----------------------------- */
class ChatGuiWithMenu {
    private JFrame frame;
    private JTextPane chatPane;
    private JTextField inputField;
    private JButton sendBtn;
    private ChatbotEngine engine;
    private SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm:ss");

    ChatGuiWithMenu(ChatbotEngine engine) {
        this.engine = engine;
        buildUi();
    }

    private void buildUi() {
        frame = new JFrame("AI Chatbot — Ankit");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 600);
        frame.setLocationRelativeTo(null);

        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(chatPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        inputField = new JTextField();
        sendBtn = new JButton("Send");

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendBtn, BorderLayout.EAST);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadItem = new JMenuItem("Load FAQs from faq.txt");
        JMenuItem saveItem = new JMenuItem("Save FAQs to faq.txt");
        JMenuItem addFaqItem = new JMenuItem("Add FAQ");
        JMenuItem rebuildIndex = new JMenuItem("Rebuild Index");
        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(addFaqItem);
        fileMenu.addSeparator();
        fileMenu.add(rebuildIndex);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        // initial bot greeting
        appendBotText("Namaste! Main aapka AI assistant hoon. Aap mujhse sawal pooch sakte hain (e.g., 'How to reset password?', 'What is TF IDF?').");

        // events
        sendBtn.addActionListener(e -> sendUserText());
        inputField.addActionListener(e -> sendUserText());

        loadItem.addActionListener(e -> {
            engine.loadFaqFile("faq.txt");
            appendBotText("FAQs loaded from faq.txt (if file existed). Index rebuilt.");
        });

        saveItem.addActionListener(e -> {
            engine.saveFaqFile("faq.txt");
            appendBotText("FAQs saved to faq.txt.");
        });

        addFaqItem.addActionListener(e -> {
            showAddFaqDialog();
        });

        rebuildIndex.addActionListener(e -> {
            engine.buildIndex();
            appendBotText("Index rebuilt with current FAQs.");
        });

        // shortcut: Ctrl+L clears chat
        inputField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK), "clear");
        inputField.getActionMap().put("clear", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                chatPane.setText("");
            }
        });
    }

    void show() {
        frame.setVisible(true);
        inputField.requestFocusInWindow();
    }

    private void sendUserText() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;
        appendUserText(text);
        inputField.setText("");
        SwingUtilities.invokeLater(() -> {
            String reply = engine.respond(text);
            // small delay to simulate typing
            Timer t = new Timer(250, evt -> appendBotText(reply));
            t.setRepeats(false);
            t.start();
        });
    }

    private void showAddFaqDialog() {
        JPanel panel = new JPanel(new BorderLayout(6,6));
        JTextField qField = new JTextField();
        JTextArea aField = new JTextArea(6, 30);
        aField.setLineWrap(true);
        aField.setWrapStyleWord(true);
        panel.add(new JLabel("Question:"), BorderLayout.NORTH);
        panel.add(qField, BorderLayout.CENTER);
        panel.add(new JLabel("Answer:"), BorderLayout.SOUTH);

        JPanel container = new JPanel(new BorderLayout(6,6));
        container.add(panel, BorderLayout.NORTH);
        container.add(new JScrollPane(aField), BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(frame, container, "Add new FAQ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String q = qField.getText().trim();
            String a = aField.getText().trim();
            if (!q.isEmpty() && !a.isEmpty()) {
                engine.addFaq(q, a);
                engine.buildIndex();
                appendBotText("Naya FAQ add ho gaya aur index rebuild hua.");
            } else {
                JOptionPane.showMessageDialog(frame, "Question aur Answer dono bharna zaroori hai.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void appendUserText(String text) {
        appendStyledText("You [" + timeFmt.format(new Date()) + "]: ", Color.BLUE, true);
        appendStyledText(text + "\n\n", Color.BLACK, false);
    }

    private void appendBotText(String text) {
        appendStyledText("Bot [" + timeFmt.format(new Date()) + "]: ", Color.MAGENTA, true);
        appendStyledText(text + "\n\n", Color.DARK_GRAY, false);
    }

    private void appendStyledText(String text, Color color, boolean bold) {
        StyledDocument doc = chatPane.getStyledDocument();
        Style style = chatPane.addStyle("Style", null);
        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, bold);
        try {
            doc.insertString(doc.getLength(), text, style);
            chatPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}

/* -----------------------------
   Chatbot Engine (NLP + TF-IDF matching)
   ----------------------------- */
class ChatbotEngine {
    private List<FAQ> faqs = new ArrayList<>();
    private Set<String> vocabulary = new LinkedHashSet<>();
    private Map<String, Double> idf = new HashMap<>();
    private Map<FAQ, double[]> tfidfVectors = new HashMap<>();
    private NLP nlp = new NLP();
    private final double MATCH_THRESHOLD = 0.18; // tuneable

    ChatbotEngine() {}

    // load built-in FAQs (training)
    public void loadDefaultFaqs() {
        addFaq("What is your name", "Mera naam Ankit's AI Assistant hai. Aap kaise ho?");
        addFaq("How are you", "Main theek hoon — aap kaise ho?");
        addFaq("How to reset password", "Password reset karne ke liye 'Forgot Password' par click karein aur instructions follow karein.");
        addFaq("What services do you offer", "Main coding help, AI tools explanations, and tutorials provide karta hoon.");
        addFaq("How to contact support", "Aap support@example.com par email bhej sakte hain ya phone par call karein: 9876543210.");
        addFaq("What is java", "Java ek object-oriented programming language hai jo cross-platform applications banane ke liye use hoti hai.");
        addFaq("How to compile java", "Terminal mein: javac FileName.java aur run karne ke liye: java FileName");
        addFaq("What is machine learning", "Machine learning ek technique hai jisme models data se patterns seekhte hain aur predictions karte hain.");
        addFaq("How to create account", "Create account ke liye signup page par jaakar required fields fill karein aur submit karein.");
        addFaq("What is tf idf", "TF–IDF ek text representation technique hai jo word importance ko measure karti hai based on frequency and document rarity.");

        buildIndex();
    }

    // load FAQ file if exists (safe wrapper)
    public void loadFaqFileIfExists(String filename) {
        Path p = Paths.get(filename);
        if (Files.exists(p)) {
            loadFaqFile(filename);
        }
    }

    // load from file (format: question|answer per line)
    public void loadFaqFile(String filename) {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            System.err.println("FAQ file not found: " + filename);
            return;
        }
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    addFaq(parts[0].trim(), parts[1].trim());
                    count++;
                }
            }
            System.out.println("Loaded " + count + " FAQs from " + filename);
            buildIndex();
        } catch (IOException e) {
            System.err.println("Error loading FAQs: " + e.getMessage());
        }
    }

    // save current FAQs to file (overwrites)
    public void saveFaqFile(String filename) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filename))) {
            for (FAQ f : faqs) {
                // write in safe format, replace newlines in answer
                String q = f.question.replaceAll("\\r?\\n", " ");
                String a = f.answer.replaceAll("\\r?\\n", " ");
                bw.write(q + " | " + a);
                bw.newLine();
            }
            System.out.println("FAQs saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving FAQs: " + e.getMessage());
        }
    }

    // add an FAQ pair
    public void addFaq(String question, String answer) {
        FAQ f = new FAQ(question, answer);
        faqs.add(f);
    }

    // rebuild vocabulary, idf and TF-IDF vectors
    public void buildIndex() {
        vocabulary.clear();
        List<List<String>> tokenDocs = new ArrayList<>();
        for (FAQ f : faqs) {
            List<String> tokens = nlp.tokenize(f.question);
            f.tokens = tokens;
            tokenDocs.add(tokens);
            vocabulary.addAll(tokens);
        }

        // compute idf (smoothed)
        idf.clear();
        int N = tokenDocs.size();
        for (String term : vocabulary) {
            int docCount = 0;
            for (List<String> doc : tokenDocs) {
                if (doc.contains(term)) docCount++;
            }
            double value = Math.log((N + 1.0) / (docCount + 1.0)) + 1.0; // smoothed
            idf.put(term, value);
        }

        // compute TF-IDF vectors
        tfidfVectors.clear();
        List<String> vocabList = new ArrayList<>(vocabulary);
        for (FAQ f : faqs) {
            double[] vec = new double[vocabList.size()];
            Map<String, Integer> tf = new HashMap<>();
            for (String t : f.tokens) tf.put(t, tf.getOrDefault(t, 0) + 1);
            for (int i = 0; i < vocabList.size(); i++) {
                String term = vocabList.get(i);
                double tfVal = tf.getOrDefault(term, 0);
                vec[i] = tfVal * idf.getOrDefault(term, 0.0);
            }
            normalize(vec);
            tfidfVectors.put(f, vec);
            f.vector = vec;
        }
    }

    // respond to input text
    public String respond(String text) {
        // rule-based
        String rule = ruleBasedResponse(text);
        if (rule != null) return rule;

        // tokenize
        List<String> tokens = nlp.tokenize(text);
        if (tokens.isEmpty()) return "Mujhe samajh nahi aaya—kripya thoda aur detail dein.";

        // ensure index
        if (vocabulary.isEmpty()) buildIndex();
        List<String> vocabList = new ArrayList<>(vocabulary);
        double[] qvec = new double[vocabList.size()];
        Map<String, Integer> tf = new HashMap<>();
        for (String t : tokens) tf.put(t, tf.getOrDefault(t, 0) + 1);
        for (int i = 0; i < vocabList.size(); i++) {
            String term = vocabList.get(i);
            double tfVal = tf.getOrDefault(term, 0);
            qvec[i] = tfVal * idf.getOrDefault(term, 0.0);
        }
        normalize(qvec);

        // match
        double bestScore = 0.0;
        FAQ best = null;
        for (Map.Entry<FAQ, double[]> e : tfidfVectors.entrySet()) {
            double sim = cosineSimilarity(qvec, e.getValue());
            if (sim > bestScore) {
                bestScore = sim;
                best = e.getKey();
            }
        }

        if (bestScore >= MATCH_THRESHOLD && best != null) {
            return best.answer + " (confidence: " + String.format("%.2f", bestScore) + ")";
        } else {
            // suggestions
            List<ScoredFAQ> sug = topKSimilar(qvec, 3);
            StringBuilder sb = new StringBuilder();
            sb.append("Mujhe exact jawab nahi mila. Shayad aap inme se puchna chahein:\n");
            for (ScoredFAQ s : sug) {
                sb.append(" • ").append(s.faq.question).append(" (sim: ").append(String.format("%.2f", s.score)).append(")\n");
            }
            sb.append("Ya phir apna sawaal thoda aur detail mein puchiye.");
            return sb.toString();
        }
    }

    private List<ScoredFAQ> topKSimilar(double[] qvec, int k) {
        PriorityQueue<ScoredFAQ> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> -a.score));
        for (Map.Entry<FAQ, double[]> e : tfidfVectors.entrySet()) {
            double sim = cosineSimilarity(qvec, e.getValue());
            pq.add(new ScoredFAQ(e.getKey(), sim));
        }
        List<ScoredFAQ> out = new ArrayList<>();
        for (int i = 0; i < k && !pq.isEmpty(); i++) out.add(pq.poll());
        return out;
    }

    // rule-based responses
    private String ruleBasedResponse(String text) {
        String t = text.toLowerCase();
        if (t.matches(".*\\b(hi|hello|hey|namaste|namaskar)\\b.*")) {
            return "Hello! Main aapki kaise madad karun?";
        }
        if (t.matches(".*\\b(thank|thanks|shukriya|dhanyavaad)\\b.*")) {
            return "Aapka swagat hai! Aur kuch chahiye to bataiye.";
        }
        if (t.matches(".*\\b(bye|goodbye|see you|phir milenge|bye-bye)\\b.*")) {
            return "Alvida! Aapka din shubh ho.";
        }
        return null;
    }

    // cosine similarity
    private double cosineSimilarity(double[] a, double[] b) {
        if (a == null || b == null || a.length != b.length) return 0.0;
        double dot = 0.0, na = 0.0, nb = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        if (na == 0.0 || nb == 0.0) return 0.0;
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }

    // normalize vector
    private void normalize(double[] v) {
        double sum = 0.0;
        for (double d : v) sum += d * d;
        if (sum == 0.0) return;
        double norm = Math.sqrt(sum);
        for (int i = 0; i < v.length; i++) v[i] /= norm;
    }
}

/* -----------------------------
   Supporting classes
   ----------------------------- */
class FAQ {
    String question;
    String answer;
    List<String> tokens = new ArrayList<>();
    double[] vector;

    FAQ(String q, String a) {
        this.question = q;
        this.answer = a;
    }
}

class ScoredFAQ {
    FAQ faq;
    double score;
    ScoredFAQ(FAQ f, double s) { this.faq = f; this.score = s; }
}

/* -----------------------------
   Minimal NLP utilities
   ----------------------------- */
class NLP {
    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
            "a","an","the","is","are","was","were","in","on","at","to","for","of","and","or","but",
            "what","how","why","do","does","did","i","you","we","they","he","she","it","my","your",
            "please","me","can","could","would","will","shall","this","that","these","those","from",
            "by","with","about","as","be","have","has","had","so","if","then","its","isnt","dont"
    ));

    // lowercase, remove punctuation, tokenize, remove stopwords, naive stem
    public List<String> tokenize(String text) {
        if (text == null) return Collections.emptyList();
        String t = text.toLowerCase();
        t = t.replaceAll("[^a-z0-9\\s]", " ");
        String[] parts = t.split("\\s+");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            p = p.trim();
            if (p.isEmpty()) continue;
            if (STOPWORDS.contains(p)) continue;
            p = naiveStem(p);
            if (p.length() > 0) out.add(p);
        }
        return out;
    }

    // very naive stemmer
    private String naiveStem(String w) {
        if (w.endsWith("ing") && w.length() > 4) return w.substring(0, w.length() - 3);
        if (w.endsWith("ed") && w.length() > 3) return w.substring(0, w.length() - 2);
        if (w.endsWith("s") && w.length() > 3) return w.substring(0, w.length() - 1);
        return w;
    }
}
