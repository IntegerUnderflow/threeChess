package threeChess;

import java.awt.*;
import javax.swing.JFrame;

public class ThreeChessDisplay extends JFrame{

  private static final Color DARKRED = new Color(127,0,0);
  private static final Color RED = new Color(255,102,102);
  private static final Color LIGHTRED = new Color(255,204,204);
  private static final Color DARKGREEN = new Color(0,127,0);
  private static final Color GREEN = new Color(102,255,102);
  private static final Color LIGHTGREEN = new Color(204,255,204);
  private static final Color DARKBLUE = new Color(0,0,127);
  private static final Color BLUE = new Color(102,102,255);
  private static final Color LIGHTBLUE = new Color(204,204,255);
  private static final int FONTSIZE = 32;
  private Square[] squares;
  private String[] players;
  private Board board;
  private int size = 800;
  private static int[][][] flanks;
  //set up 96 polygons here?
  //no use board/Position to build them
  private class Square{
    int[] xs;//4 x-coords
    int[] ys;//4 y-coords
    Piece piece; //piece in the square or null if empty
    boolean coloured;// for white or not
    Colour colour;

    /**
     * Constructs a square corresponding to the position given.
     * This calculates the cartesian coordinates of the corners of the position, 
     * and records the squares colour and parity
     * **/
    public Square(Position pos){
      colour = pos.getColour();
      coloured = pos.evenParity();
      //calculate coords for poly point and center
      int r = pos.getRow();
      int c = pos.getColumn();
      xs = new int[4]; ys = new int[4]; //lower left, lower right, upper right, upper left.
      int[] left = flanks[colour.ordinal()][(c<4?0:1)];//coords of left margin
      int[] right = flanks[colour.ordinal()][(c<4?1:2)];//coords of right margin.
      int[] baseLine = new int[4];
      int[] topLine = new int[4];
      baseLine[0] = left[0]+((left[2]-left[0])/4)*r;
      baseLine[1] = left[1]+((left[3]-left[1])/4)*r;
      baseLine[2] = right[0]+((right[2]-right[0])/4)*r;
      baseLine[3] = right[1]+((right[3]-right[1])/4)*r;
      topLine[0] = left[0]+((left[2]-left[0])/4)*(r+1);
      topLine[1] = left[1]+((left[3]-left[1])/4)*(r+1);
      topLine[2] = right[0]+((right[2]-right[0])/4)*(r+1);
      topLine[3] = right[1]+((right[3]-right[1])/4)*(r+1);
      xs[0] = baseLine[0]+((baseLine[2]-baseLine[0])/4)*(c%4); ys[0] = baseLine[1]+((baseLine[3]-baseLine[1])/4)*(c%4);  //bottom left
      xs[1] = baseLine[0]+((baseLine[2]-baseLine[0])/4)*(c%4+1); ys[1] = baseLine[1]+((baseLine[3]-baseLine[1])/4)*(c%4+1);  //bottom right
      xs[2] = topLine[0]+((topLine[2]-topLine[0])/4)*(c%4+1); ys[2] = topLine[1]+((topLine[3]-topLine[1])/4)*(c%4+1);  //top right
      xs[3] = topLine[0]+((topLine[2]-topLine[0])/4)*(c%4); ys[3] = topLine[1]+((topLine[3]-topLine[1])/4)*(c%4);  //top left
    }

    // just returns the mean of all the coordinates
    private int[] getCentre(){
      int[] centre = new int[2];
      for(int i = 0; i<4; i++){
        centre[0]+=xs[i];
        centre[1]+=ys[i];
      }
      centre[0]=centre[0]/4-FONTSIZE/2;
      centre[1] = centre[1]/4+FONTSIZE/2;
      return centre;
    }

    /**Sets the piece of the square to the specified piece, or null if square is unoccupied**/
    public void setPiece(Piece piece){this.piece = piece;}

    //creates a colour for pieces and boards squares
    private Color getColour(Colour col, boolean piece, boolean coloured){
      switch(col){
        case RED: return piece? DARKRED: coloured? RED: LIGHTRED;
        case GREEN: return piece? DARKGREEN: coloured? GREEN: LIGHTGREEN;
        case BLUE: return piece? DARKBLUE: coloured? BLUE: LIGHTBLUE;
        default: return null;
      }
    }      

    /**
     * Renders the square wit a black border, 
     * and a light colour if parity is even, 
     * and the piece in the board.
     * The graphics object should be updated after this method is called.
     * **/  
    public void draw(Graphics g){
      g.setColor(getColour(colour, false, coloured));
      g.fillPolygon(xs,ys,4);
      g.setColor(Color.BLACK);
      g.drawPolygon(xs, ys, 4);
      if(piece!=null){
        int[] pos = getCentre();
        g.setColor(getColour(piece.getColour(), true, true));
        g.drawString(""+piece.getType().getChar(),pos[0],pos[1]);
      }
    }


  }

  /**
   * Creates a graphical representation of a threeChess board
   * @param board the game state to be represented
   * @param bluePlayer the name of the blue player
   * @param greenPlayer the name of the green player
   * @param redPlayer the name of the red player
   * **/  
  public ThreeChessDisplay(Board board, String bluePlayer, String greenPlayer, String redPlayer){
    super("ThreeChess");
    this.board = board;
    setSize(size,size);
    setVisible(true);
    setFlanks(size);
    players = new String[3];
    squares = new Square[96];
    for(int i=0; i<96; i++) squares[i] = new Square(Position.values()[i]);
    players[0] = bluePlayer;
    players[1] = greenPlayer;
    players[2] = redPlayer;
    repaint();
  }

  //updates the graphics component, then the window
  public void paint(Graphics g){
    setBackground(Color.LIGHT_GRAY);
    g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, FONTSIZE/2));
    Graphics2D g2 = (Graphics2D) g;
    g2.setStroke(new BasicStroke(3));
    int h_unit = size/20;
    int v_unit =(int) (Math.sqrt(3)*h_unit);
    for(int i=0; i<8; i++){
      String label = ""+((char)(65+i));
      g.setColor(DARKBLUE);
      g.drawString(label, (27-2*i)*h_unit/2,7*v_unit/4);
      g.setColor(DARKRED);
      g.drawString(label,(4+i)*h_unit/2,(13+i)*v_unit/2);
      g.setColor(DARKGREEN);
      g.drawString(label,(29+i)*h_unit/2,(20-i)*v_unit/2);
    }
    for(int i = 0; i<4; i++){
      g.setColor(Color.BLUE);
      g.drawString(""+(i+1), (10-i)*h_unit/2, (4+i)*v_unit/2);
      g.drawString(""+(i+1), (30+i)*h_unit/2, (4+i)*v_unit/2);
      g.setColor(Color.RED);
      g.drawString(""+(i+1),(3+i)*h_unit/2, (11-i)*v_unit/2);
      g.drawString(""+(i+1),(13+2*i)*h_unit/2,21*v_unit/2);
      g.setColor(Color.GREEN);
      g.drawString(""+(i+1),(27-2*i)*h_unit/2, 21*v_unit/2);
      g.drawString(""+(i+1),(37-i)*h_unit/2,(11-i)*v_unit/2);
    }
    for(Position pos: Position.values())squares[pos.ordinal()].setPiece(board.getPiece(pos));
    g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, FONTSIZE));
    for(Square sq: squares) sq.draw(g);
    g.setColor(Color.BLUE);
    g.drawString(players[0]+": "+board.getTimeLeft(Colour.BLUE)/1000,9*h_unit,v_unit);
    g.setColor(Color.GREEN);
    g.drawString(players[1]+": "+board.getTimeLeft(Colour.GREEN)/1000,16*h_unit,9*v_unit);
    g.setColor(Color.RED);
    g.drawString(players[2]+": "+board.getTimeLeft(Colour.RED)/1000,h_unit,9*v_unit);
  }

  //calculates the coordinates of flanks for computing square coordinates.
  private static void setFlanks(int size){
    int h_unit = size/10;
    int v_unit = (int) (h_unit*Math.sqrt(3));
    flanks = new int[3][3][4];
    flanks[0][0][0] = 7*h_unit; flanks[0][0][1] = v_unit; flanks[0][0][2] = 8*h_unit; flanks[0][0][3] = 2*v_unit;// (x1,y1,x2,y2) coords of the left flank of the blue section of board
    flanks[0][1][0] = 5*h_unit; flanks[0][1][1] = v_unit; flanks[0][1][2] = 5*h_unit; flanks[0][1][3] = 3*v_unit;// (x1,y1,x2,y2) coords of the middle line of the blue section of board
    flanks[0][2][0] = 3*h_unit; flanks[0][2][1] = v_unit; flanks[0][2][2] = 2*h_unit; flanks[0][2][3] = 2*v_unit;// (x1,y1,x2,y2) coords of the right flank of the blue section of board
    flanks[1][0][0] = 7*h_unit; flanks[1][0][1] = 5*v_unit; flanks[1][0][2] = 5*h_unit; flanks[1][0][3] = 5*v_unit;// (x1,y1,x2,y2) coords of the left flank of the green section of board
    flanks[1][1][0] = 8*h_unit; flanks[1][1][1] = 4*v_unit; flanks[1][1][2] = 5*h_unit; flanks[1][1][3] = 3*v_unit;// (x1,y1,x2,y2) coords of the middle line of the green section of board
    flanks[1][2][0] = 9*h_unit; flanks[1][2][1] = 3*v_unit; flanks[1][2][2] = 8*h_unit; flanks[1][2][3] = 2*v_unit;// (x1,y1,x2,y2) coords of the right flank of the green section of board
    flanks[2][0][0] = h_unit; flanks[2][0][1] = 3*v_unit; flanks[2][0][2] = 2*h_unit; flanks[2][0][3] = 2*v_unit;// (x1,y1,x2,y2) coords of the left flank of the red section of board
    flanks[2][1][0] = 2*h_unit; flanks[2][1][1] = 4*v_unit; flanks[2][1][2] = 5*h_unit; flanks[2][1][3] = 3*v_unit;// (x1,y1,x2,y2) coords of the middle line of the red section of board
    flanks[2][2][0] = 3*h_unit; flanks[2][2][1] = 5*v_unit; flanks[2][2][2] = 5*h_unit; flanks[2][2][3] = 5*v_unit;// (x1,y1,x2,y2) coords of the right flank of the red section of board
  }

}




