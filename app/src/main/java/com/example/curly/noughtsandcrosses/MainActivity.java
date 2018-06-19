package com.example.curly.noughtsandcrosses;

import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.data;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.example.curly.noughtsandcrosses.R.id.btn1;
import static com.example.curly.noughtsandcrosses.R.id.txtTurn;
import static com.example.curly.noughtsandcrosses.Turn.nought;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    Button btn0;
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;
    Button btn6;
    Button btn7;
    Button btn8;
    Button[][] buttons = new Button[3][3];

    Button btnNewGame;
    Button btnResetScores;

    final Square[][] squares = new Square[3][3];
    Turn turn = nought;
    int score1 = 0;
    int score2 = 0;
    Square winner = Square.empty;
    int emptySquares = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        // Set color of title bar
//        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
//        TextView abTitle = (TextView) findViewById(titleId);
//        abTitle.setTextColor(getResources().getColor(R.color.colorWhite));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // set item as selected to persist highlight
                menuItem.setChecked(true);
                // close drawer when item is tapped
                drawerLayout.closeDrawers();

                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here


                return true;
            }
        });

        btn0 = (Button) findViewById(R.id.btn0);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        btn6 = (Button) findViewById(R.id.btn6);
        btn7 = (Button) findViewById(R.id.btn7);
        btn8 = (Button) findViewById(R.id.btn8);
        buttons = new Button[][]{{btn0, btn1, btn2}, {btn3, btn4, btn5}, {btn6, btn7, btn8}};

        View.OnClickListener btnOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rawId = v.getId();
                int btnId = getBtnId(rawId);

                attemptMove(btnId);
            }
        };

        btnNewGame = (Button) findViewById(R.id.btnNewGame);
        btnResetScores = (Button) findViewById(R.id.btnResetScores);

        btn0.setOnClickListener(btnOnClick);
        btn1.setOnClickListener(btnOnClick);
        btn2.setOnClickListener(btnOnClick);
        btn3.setOnClickListener(btnOnClick);
        btn4.setOnClickListener(btnOnClick);
        btn5.setOnClickListener(btnOnClick);
        btn6.setOnClickListener(btnOnClick);
        btn7.setOnClickListener(btnOnClick);
        btn8.setOnClickListener(btnOnClick);

        btnNewGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initNewGame();
            }
        });
        btnResetScores.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetScores();
            }
        });

        initNewGame();
        resetScores();
        updateDisplay();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initNewGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setClickable(true);
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                squares[i][j] = Square.empty;
            }
        }
        turn = Turn.nought;
        winner = Square.empty;
        emptySquares = 9;
        updateDisplay();
    }

    private void resetScores() {
        score1 = 0;
        score2 = 0;
        initNewGame();
    }

    private int getBtnId(int rawId) {
        int btnId = 0;
        if (rawId == R.id.btn1) {
            btnId = 1;
        } else if (rawId == R.id.btn2) {
            btnId = 2;
        } else if (rawId == R.id.btn3) {
            btnId = 3;
        } else if (rawId == R.id.btn4) {
            btnId = 4;
        } else if (rawId == R.id.btn5) {
            btnId = 5;
        } else if (rawId == R.id.btn6) {
            btnId = 6;
        } else if (rawId == R.id.btn7) {
            btnId = 7;
        } else if (rawId == R.id.btn8) {
            btnId = 8;
        }
        return btnId;
    }

    private void attemptMove(int btnId) {
        int row = btnId / 3;
        int col = btnId % 3;

        Square sq = squares[row][col];

        if (sq == Square.empty) {
            switch (turn) {
                case nought:
                    sq = Square.nought;
                    break;
                case cross:
                    sq = Square.cross;
                    break;
                default:
                    break;
            }
            squares[row][col] = sq;
            checkWin();
            switch (winner) {
                case nought:
                    score1++;
                    Toast.makeText(getApplicationContext(), "Nought wins!", Toast.LENGTH_SHORT).show();
                    endGame();
                    break;
                case cross:
                    score2++;
                    Toast.makeText(getApplicationContext(), "Cross wins!", Toast.LENGTH_SHORT).show();
                    endGame();
                    break;
                default:
                    break;
            }

            toggleTurn();
            emptySquares--;
        } else {
            return;
        }
        updateDisplay();
    }

    private void endGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setClickable(false);
            }
        }
    }

    private void checkWin() {
        if (squares[0][0] != Square.empty && (squares[0][0] == squares[1][1]) && (squares[0][0] == squares[2][2])) {
            winner = squares[0][0];
        } else if (squares[2][0] != Square.empty && (squares[2][0] == squares[1][1]) && (squares[2][0] == squares[0][2])) {
            winner = squares[2][0];
        }
        for (int i = 0; i < 3; i++) {
            if (squares[i][0] != Square.empty && (squares[i][0] == squares[i][1]) && (squares[i][0] == squares[i][2])) {
                winner = squares[i][0];
            } else if (squares[0][i] != Square.empty && (squares[0][i] == squares[1][i]) && (squares[0][i] == squares[2][i])) {
                winner = squares[0][i];
            }
        }
    }

    private void updateDisplay() {
        TextView txtP1Score = (TextView) findViewById(R.id.txtP1Score);
        TextView txtP2Score = (TextView) findViewById(R.id.txtP2Score);
        TextView txtTurn = (TextView) findViewById(R.id.txtTurn);
        txtP1Score.setText("Player 1: " + score1);
        txtP2Score.setText("Player 2: " + score2);

        switch (turn) {
            case nought:
                txtTurn.setText("Nought to play");
                break;
            case cross:
                txtTurn.setText("Cross to play");
                break;
        }

        switch (winner) {
            case nought:
                txtTurn.setText("Nought wins!");
                break;
            case cross:
                txtTurn.setText("Cross wins!");
                break;
            case empty:
                if (emptySquares == 0) {
                    txtTurn.setText("Stalemate!");
                }
                break;
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                switch (squares[i][j]) {
                    case empty:
                        buttons[i][j].setText("");
                        break;
                    case nought:
                        buttons[i][j].setText("O");
                        buttons[i][j].setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                        break;
                    case cross:
                        buttons[i][j].setText("X");
                        buttons[i][j].setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void toggleTurn() {
        switch (turn) {
            case nought:
                turn = Turn.cross;
                break;
            case cross:
                turn = Turn.nought;
                break;
            default:
                break;
        }
    }
}
