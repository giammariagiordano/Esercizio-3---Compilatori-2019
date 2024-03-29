import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * classe che implementa un analizzatore lessicale. Class Lexical Analyzer
 * 
 * @author Giordano Giamaria
 * @version 0.1
 */

public class LexicalAnalyzer {
  private static HashMap<String, Token> stringTable;
  int state;
  static int position;
  static String app = "";
  char character = ' ';
  String lessema = "";
  String type = "";
  Token toReturn;

  BufferedReader buffer;

  /**
   * Costruttore del lexical Analyzer. Crea una HashMap e inserisce i token if, then ed else setta
   * state = 0 setta position = -1 e la stringa che conterrà il file = vuoto
   */
  public LexicalAnalyzer() throws IOException {
    state = 0;
    position = -1;
    lessema = "";
    stringTable = new HashMap<String, Token>();
    stringTable.put("if", new Token("IF"));
    stringTable.put("then", new Token("THEN"));
    stringTable.put("else", new Token("ELSE"));
    stringTable.put("while", new Token("WHILE"));
    stringTable.put("for", new Token("FOR"));
  }

  /**
   * nextToken. Analizza da un buffer carattere per carattere ed individua il token corrispondente
   *
   * @return Token che corrisponde al lessema letto.
   * @throws Exception 
   */
  public Token nextToken() throws Exception {
    while (true) {
      switch (state) {
        case 0:
          character = (char) nextCharacter();
          if (character == '\0') {
           // System.out.println("fine");
            return null;
            //throw new NullPointerException("Fine dell'input");
          } else {
            if (character == '<') {
              state = 1;
              type = "relop";
            } else if (character == '=') {
              state = 5;
              type = "relop";
            } else if (character == '>') {
              state = 6;
              type = "relop";
            } else if (Character.isAlphabetic(character)) { // provo a vedere se è una un id/key
              lessema += character;
              state = 9;
              type = "ID";
            } else if (Character.isDigit(character)) { // provo a vedere se è un numero
              lessema += character;
              state = 13;
              type = "NUMBER";
            }
            else if (Character.isWhitespace(character)) {
              state = 23;
            }
            else if(character == '(' || character == ')' || character == '[' || character == ']'
                || character == ',' || character == ';' || character == '.' || character == ':') {
                  lessema += character;
                  type = "SEPARATOR";
                  state = 25;
            }
            else if (character == '+') {
              state = 27;
            }
            else if (character == '-') {
              state = 28;
            }
            else {
              // stato pozzo
              state = 99;
            }
          }
          break;
        case 1:
          character = (char) nextCharacter();
          if (character == '=') {
            state = 2;
          } else if (character == '>') {
            state = 3;
          } 
          else if (character == '-') {
            state = 26;
          }
          else {
            state = 4;
          }
          break;
        case 2:
          state = 0;
          return new Token(type, "LE");
        case 3:
          state = 0;
          return new Token(type, "NE");
        case 4:
          retrack();
          state = 0;
          return new Token(type, "LT");
        case 5:
          state = 0;
          return new Token(type, "EQ");
        case 6:
          character = (char) nextCharacter();
          if (character == '=') {
            state = 7;
          } else {
            state = 8;
          }
          break;
        case 7:
          state = 0;
          return new Token(type, "GE");
        case 8:
          retrack();
          state = 0;
          return new Token(type, "GT");
        case 9:
          // da 9 a 11 riconosco gli ID
          character = (char) nextCharacter();
          if (Character.isWhitespace(character)) {
            state = 11;
          } else if (Character.isLetterOrDigit(character)) {
            state = 10;
            lessema += character;
          } else {
            state = 0;
            retrack();
            toReturn = new Token(type,lessema);
            lessema = "";
            return toReturn;
          }

          break;
        case 10:
          character = (char) nextCharacter();
          if (Character.isWhitespace(character)) {
            state = 22;
          } else if (Character.isLetterOrDigit(character)) {
            state = 10;
            lessema += character;
          } else {
            state = 11;
          }
          break;
        case 11:
          retrack();
          state = 0;
          toReturn = installID(type, lessema);
          lessema = "";
          return toReturn;
        case 12:
          // da 12 a 21 riconosco i digits
          character = (char) nextCharacter();
          if (Character.isDigit(character)) {
            state = 13;
            lessema += character;
          }
          break;
        case 13:
          character = (char) nextCharacter();
          if (Character.isDigit(character)) {
            state = 13;
            lessema += character;

          } else if (character == '.') {
            state = 14;
            lessema += character;
          } else if (character == 'E' || character == 'e') {
            state = 16;
            lessema += character;
          } else {
            state = 20;
          }
          break;
        case 14:
          character = (char) nextCharacter();
          if (Character.isDigit(character)) {
            state = 15;
            lessema += character;

          }
          break;
        case 15:
          character = (char) nextCharacter();
          if (Character.isDigit(character)) {
            state = 15;
            lessema += character;
          } else {
            state = 21;
          }
          break;
        case 16:
          character = (char) nextCharacter();
          if (character == '+' || character == '-') {
            state = 17;
            lessema += character;
          } else if (Character.isDigit(character)) {
            state = 18;
            lessema += character;
          }

          break;
        case 17:
          character = (char) nextCharacter();
          if (Character.isDigit(character)) {
            state = 18;
            lessema += character;

          }
          break;
        case 18:
          character = (char) nextCharacter();
          if (Character.isDigit(character)) {
            state = 18;
            lessema += character;

          } else {
            state = 19;
          }
          break;
        case 19:
        case 20:
        case 21:
          retrack();
          state = 0;
          toReturn = installID(type, lessema);
          lessema = "";
          return toReturn;

        case 22:
          // da 22 a 24 i caratteri bianchi
          character = (char) nextCharacter();
          if (Character.isWhitespace(character)) {
            state = 23;
          } else {
            state = 0;
            retrack();
            toReturn = installID(type, lessema);
            lessema = "";
            return toReturn;
          }
          break;
        case 23:
          character = (char) nextCharacter();
          if (Character.isWhitespace(character)) {
            state = 23;
          } else {
            state = 24;
          }
          break;
        case 24:
          retrack();
          state = 0;
          break;
        case 25:
          toReturn = new Token(type, lessema);
          lessema = "";
          state = 0;
          return toReturn;
        case 26: 
          character = (char) nextCharacter();
              if (character == '-') {
                state = 0;
                return new Token("ASSIGN");
              }
              else {
                retrack();
                retrack();
                state = 0;
                return new Token(type,"LT"); 
              }
        case 27: 
              state = 0;
              return new Token("PLUS");
        case 28:
            state = 0;
            return new Token ("MINUS");
        case 99: {
          state = 0;
          return new Token("Errore di sintassi");    
        }
        default:
          break;
      }
    }
  }

  /**
   * Inizializza il buffer. The long and detailed explanation what the method does.
   * 
   * @param fp
   * @return true se è stato creato correttamente, false altrimenti.
   */

  public Boolean initialize(String filePath) throws IOException {
    app = "";
    buffer = new BufferedReader(new FileReader(filePath));
    int test;
    while ((test = buffer.read()) != -1) {
      /*
       * if (test == -1) { return false; // fine del file }
       */
      app += (char) test;
    }
    app += '\0';
   // System.out.println("app: "+app);
    return true;
  }

  private Token installID(String id, String lessema) {
    Token token;
    if (stringTable.containsKey(lessema)) {
      return stringTable.get(lessema);
    } else {
      token = new Token(id, lessema);
      stringTable.put(lessema, token);
      return token;
    }
  }
  public void printHashMap() {
    for (Token e : stringTable.values()) {
      System.out.println("KEY: "+e.getName()+" attr: "+e.getAttribute());
    }
  }

  // ritorma il prossimo carattere nella stringa
  private int nextCharacter() {
    position++;
    return app.charAt(position);
  }

  private void retrack() throws IOException {
    position--;
  }
}
