=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 1200 Game Project README
PennKey: 18720296
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. 2D Array

  2. Collections

  3. Inheritance or Subtyping

  4. Complex Game Logic

===============================
=: File Structure Screenshot :=
===============================
- Include a screenshot of your project's file structure. This should include
  all of the files in your project, and the folders they are in. You can
  upload this screenshot in your homework submission to gradescope, named 
  "file_structure.png".

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
Bishop - The Bishop class represents a chess bishop piece, managing its type,
image, and movement logic.
ChessBoard -The ChessBoard class initializes an 8x8 grid with alternating light
and dark colors to create a chess board.
ChessGamePanel - The main class which contains all the state and has all the
necessary methods for implementation of chess
King - The King class represents a chess bishop piece, managing its type,
       image, and movement logic.
Knight - The Knight class represents a chess bishop piece, managing its type,
         image, and movement logic.
MouseHandler -The MouseHandler class tracks mouse actions like press, release, drag, and movement, storing the mouse's coordinates and press state.
Pawn - The Pawn class represents a chess bishop piece, managing its type,
                image, and movement logic.
Piece - The Piece class represents a chess piece, managing its properties (type, position, color, image), movement logic (valid moves, board constraints, and hit detection), and visual rendering on the chessboard.
Queen - The Queen class represents a chess bishop piece, managing its type,
               image, and movement logic.
Rook - The Rook class represents a chess bishop piece, managing its type,
              image, and movement logic.
RunChess - The RunChess class initializes and runs the chess game by creating a
JFrame, setting up the ChessGamePanel, and starting the game loop.
StartMenu - The StartMenu class contains the Menu which is showed in the
beginning of the game.

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?

Yeah, implementing all the complex logics was a hard task, specially en passant
and castling as both of them were also to be handled by their specific piece
subclasses. Implementing checkmate was also a tough job.

- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?
I think there is good separation of functionality. One thing I guess I would do
is make my validations more robust and crisp, right now I think there is a much
better way of writing them.


========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.
