package eth.fingerfixer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

// 음표 종류와 계이름을 입력받아 음표를 그려주는 클래스

public class Note {
                // 부모 액티비티 객체, 음표를 그릴 레이아웃,    음표/쉼표     옥타브        음표종류,      음이름,         옵션
    public Note(Activity activity, LinearLayout linearLayout, char type, char octave, char noteType, char pitchName, char option) {
        // 음표 이미지가 들어갈 이미지 뷰
        ImageView note = new ImageView(activity);

        // 음표의 크기
        LinearLayout.LayoutParams params_note = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params_note.width = 32;
        params_note.height = 60;

        // 음의 위치를 정해줌 (5선지의 어디에 위치할건가)
        selectTone(note, params_note, octave, noteType, pitchName, option);
        params_note.weight = 1;

        // 음표 정해줌
        selectNoteImage(note, params_note, type, octave, noteType, pitchName, option);

        note.setLayoutParams(params_note);
        linearLayout.addView(note);
    }

                                                              // 몇분에    몇박자
    public Note(Activity activity, FrameLayout frameLayout, int bottom, int top) {

        TextView mTop = new TextView(activity);
        TextView mBottom = new TextView(activity);

        // 박자표 크기
        FrameLayout.LayoutParams params_note_top = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params_note_top.topMargin = 17;
        params_note_top.rightMargin = 20;
        FrameLayout.LayoutParams params_note_bottom = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params_note_bottom.topMargin = 60;
        params_note_bottom.rightMargin = 20;

        mTop.setTextSize(30);
        mTop.setTypeface(null, Typeface.BOLD);
        mTop.setTextColor(Color.BLACK);
        mTop.setIncludeFontPadding(false);
        mBottom.setTextSize(30);
        mBottom.setTypeface(null, Typeface.BOLD);
        mBottom.setTextColor(Color.BLACK);
        mBottom.setIncludeFontPadding(false);

        // 박자 값 설정
        mTop.setText(String.valueOf(top));
        mBottom.setText(String.valueOf(bottom));

        // 파라미터 연결
        mTop.setLayoutParams(params_note_top);
        mBottom.setLayoutParams(params_note_bottom);

        // 그려주기
        frameLayout.addView(mTop);
        frameLayout.addView(mBottom);
    }


    public void selectTone(ImageView note, LinearLayout.LayoutParams params_note, char octave, char noteType, char pitchName, char option) {
        // 계이름 정해줌 (계이름이 아래 도이면 addNoteBar에서 적절한 처리)
        if (octave == '4')    // 높은음자리표의 4옥타브 이면
        {
            switch (pitchName) {
                case 'c':  // 도
                    params_note.topMargin = 82;
                    //params_note.width = 36;
                    //addNoteBar(noteType, note, params_note, option); // 음표에 짝대기 추가
                    break;
                case 'd':  // 레
                    params_note.topMargin = 73; break;
                case 'e':  // 미
                    params_note.topMargin = 64; break;
                case 'f':  // 파
                    params_note.topMargin = 54; break;
                case 'g':  // 솔
                    params_note.topMargin = 42; break;
                case 'a':  // 라
                    params_note.topMargin = 32; break;
                case 'b':  // 시     // 도 : 9
                    params_note.topMargin = 65;
                    break;
                default:    // 0을 포함한 에러들
            }
        }
        else if (octave == '5')
        {   // 높은음 자리표의 5옥타브 이면
            switch (pitchName) {
                case 'c':  // 도
                    params_note.topMargin = 55;break;
                case 'd':  // 레
                    params_note.topMargin = 45; break;
                case 'e':  // 미
                    params_note.topMargin = 35; break;
                case 'f':  // 파
                    params_note.topMargin = 23; break;
                case 'g':  // 솔
                    params_note.topMargin = 14; break;
                case 'a':  // 라
                    params_note.topMargin = 4;
                    //params_note.width = 36;
                    //addUpNoteBar(noteType, note, params_note, option); // 음표에 짝대기 추가
                    break;
                default:    // 0과 5옥타브 시를 포함한 에러들
                    params_note.topMargin = 40; break;
            }
        }
        if (octave == '3')    // 낮은음자리표 3옥타브 이면
        {
            switch (pitchName) {
                case 'c':  // 도
                    params_note.topMargin = 32; break;
                case 'd':  // 레
                    params_note.topMargin = 65; break;
                case 'e':  // 미
                    params_note.topMargin = 55; break;
                case 'f':  // 파
                    params_note.topMargin = 45; break;
                case 'g':  // 솔
                    params_note.topMargin = 35; break;
                case 'a':  // 라
                    params_note.topMargin = 23; break;
                case 'b':  // 시
                    params_note.topMargin = 14; break;
                default:    // 0을 포함한 에러들
            }
        }
    }


    // 음표가 어떤 모양인지 상황에 따라 판단하는 부분
    public void selectNoteImage(ImageView note, LinearLayout.LayoutParams params_note, char type, char octave, char noteType, char pitchName, char option) {
        if (type == 'M') {
            if (octave == '4') {
                switch (pitchName) {
                    case 'c':
                        addNoteBar(noteType, note, params_note, option); break;
                    case 'd': case 'e': case 'f': case 'g': case 'a':
                        setNormalNote(noteType, note, params_note, option); break;
                    case 'b':
                        setUpperNote(noteType, note, params_note, option); break;
                    default:
                        note.setImageResource(R.drawable.note_error);
                }
            } else if (octave == '5') {
                switch (pitchName) {
                    case 'c': case 'd': case 'e': case 'f': case 'g':
                        setUpperNote(noteType, note, params_note, option); break;
                    case 'a':
                        addUpNoteBar(noteType, note, params_note, option); break;
                    default:
                        note.setImageResource(R.drawable.note_error);
                }
                setUpperNote(noteType, note, params_note, option);
            } else if (octave == '3') {
                switch (pitchName) {
                    case 'c':
                        setNormalNote(noteType, note, params_note, option); break;
                    case 'd': case 'e': case 'f': case 'g': case 'a': case 'b':
                        setUpperNote(noteType, note, params_note, option); break;
                    default:
                        note.setImageResource(R.drawable.note_error);
                }
            }
            params_note.width = 48;
        } else if (type == 'R') {   // 쉼표이면
            switch (noteType) {
                case 'B':  // 온쉼표
                    note.setImageResource(R.drawable.note0r); break;
                case 'D':  // 2분쉼표
                    note.setImageResource(R.drawable.note2r); break;
                case 'F':  // 4분쉼표
                    note.setImageResource(R.drawable.note4r); break;
                case 'H':  // 8분쉼표
                    note.setImageResource(R.drawable.note8r); break;
                case 'J':  // 16분쉼표
                    note.setImageResource(R.drawable.note16r); break;
                default:    // 에러
                    note.setImageResource(R.drawable.note_error);
            }
            params_note.topMargin = 40;     // 쉼표 위치
        } else { // 에러
            note.setImageResource(R.drawable.note_error);
        }
    }

    ////////////////////////////// 실질적으로 음표의 모양을 정해주는 부분 ////////////////////////////

    // 일반적인 음표 모양
    public void setNormalNote(char noteType, ImageView note, LinearLayout.LayoutParams params_note, char option) {
        if (option == '%') {
            switch (noteType) {
                case 'A':  // 온음표 점
                    note.setImageResource(R.drawable.note0p); break;
                case 'B':  // 온음표
                    note.setImageResource(R.drawable.note0); break;
                case 'C':  // 2분음표 점
                    note.setImageResource(R.drawable.note2p); break;
                case 'D':  // 2분음표
                    note.setImageResource(R.drawable.note2); break;
                case 'E':  // 4분음표 점
                    note.setImageResource(R.drawable.note4p); break;
                case 'F':  // 4분음표
                    note.setImageResource(R.drawable.note4); break;
                case 'G':  // 8분음표 점
                    note.setImageResource(R.drawable.note8p); break;
                case 'H':  // 8분음표
                    note.setImageResource(R.drawable.note8); break;
                case 'I':  // 16분음표 점
                    note.setImageResource(R.drawable.note16p); break;
                case 'J':  // 16분음표
                    note.setImageResource(R.drawable.note16); break;
                default:    // 에러
                    note.setImageResource(R.drawable.note_error);
            }
        } else if (option == '#') {
            switch (noteType) {
                case 'A':  // 온음표 점
                    note.setImageResource(R.drawable.note0ps); break;
                case 'B':  // 온음표
                    note.setImageResource(R.drawable.note0s); break;
                case 'C':  // 2분음표 점
                    note.setImageResource(R.drawable.note2ps); break;
                case 'D':  // 2분음표
                    note.setImageResource(R.drawable.note2s); break;
                case 'E':  // 4분음표 점
                    note.setImageResource(R.drawable.note4ps); break;
                case 'F':  // 4분음표
                    note.setImageResource(R.drawable.note4s); break;
                case 'G':  // 8분음표 점
                    note.setImageResource(R.drawable.note8ps); break;
                case 'H':  // 8분음표
                    note.setImageResource(R.drawable.note8s); break;
                case 'I':  // 16분음표 점
                    note.setImageResource(R.drawable.note16ps); break;
                case 'J':  // 16분음표
                    note.setImageResource(R.drawable.note16s); break;
                default:    // 에러
                    note.setImageResource(R.drawable.note_error);
            }
            params_note.width = 48;
        } else if (option == '$') {
            switch (noteType) {
                case 'A':  // 온음표 점
                    note.setImageResource(R.drawable.note0pf); break;
                case 'B':  // 온음표
                    note.setImageResource(R.drawable.note0f); break;
                case 'C':  // 2분음표 점
                    note.setImageResource(R.drawable.note2pf); break;
                case 'D':  // 2분음표
                    note.setImageResource(R.drawable.note2f); break;
                case 'E':  // 4분음표 점
                    note.setImageResource(R.drawable.note4pf); break;
                case 'F':  // 4분음표
                    note.setImageResource(R.drawable.note4f); break;
                case 'G':  // 8분음표 점
                    note.setImageResource(R.drawable.note8pf); break;
                case 'H':  // 8분음표
                    note.setImageResource(R.drawable.note8f); break;
                case 'I':  // 16분음표 점
                    note.setImageResource(R.drawable.note16pf); break;
                case 'J':  // 16분음표
                    note.setImageResource(R.drawable.note16f); break;
                default:    // 에러
                    note.setImageResource(R.drawable.note_error);
            }
            params_note.width = 48;
        }
    }


    // 높은 음일때 음표 모양
    public void setUpperNote(char noteType, ImageView note, LinearLayout.LayoutParams params_note, char option) {
        if (option == '%') {
            switch (noteType) {
                case 'A':  // 온음표 점
                    note.setImageResource(R.drawable.unote0p); break;
                case 'B':  // 온음표
                    note.setImageResource(R.drawable.unote0); break;
                case 'C':  // 2분음표 점
                    note.setImageResource(R.drawable.note2p); break;
                case 'D':  // 2분음표
                    note.setImageResource(R.drawable.note2); break;
                case 'E':  // 4분음표 점
                    note.setImageResource(R.drawable.unote4p); break;
                case 'F':  // 4분음표
                    note.setImageResource(R.drawable.unote4); break;
                case 'G':  // 8분음표 점
                    note.setImageResource(R.drawable.unote8p); break;
                case 'H':  // 8분음표
                    note.setImageResource(R.drawable.unote8); break;
                case 'I':  // 16분음표 점
                    note.setImageResource(R.drawable.unote16p); break;
                case 'J':  // 16분음표
                    note.setImageResource(R.drawable.unote16); break;
                default:    // 에러
                    note.setImageResource(R.drawable.note_error);
            }
        } else if (option == '#') {
            switch (noteType) {
                case 'A':  // 온음표 점
                    note.setImageResource(R.drawable.unote0ps); break;
                case 'B':  // 온음표
                    note.setImageResource(R.drawable.unote0s); break;
                case 'C':  // 2분음표 점
                    note.setImageResource(R.drawable.unote2ps); break;
                case 'D':  // 2분음표
                    note.setImageResource(R.drawable.unote2s); break;
                case 'E':  // 4분음표 점
                    note.setImageResource(R.drawable.unote4ps); break;
                case 'F':  // 4분음표
                    note.setImageResource(R.drawable.unote4s); break;
                case 'G':  // 8분음표 점
                    note.setImageResource(R.drawable.unote8ps); break;
                case 'H':  // 8분음표
                    note.setImageResource(R.drawable.unote8s); break;
                case 'I':  // 16분음표 점
                    note.setImageResource(R.drawable.unote16ps); break;
                case 'J':  // 16분음표
                    note.setImageResource(R.drawable.unote16s); break;
                default:    // 에러
                    note.setImageResource(R.drawable.note_error);
            }
            params_note.width = 48;
        } else if (option == '$') {
            switch (noteType) {
                case 'A':  // 온음표 점
                    note.setImageResource(R.drawable.unote0pf); break;
                case 'B':  // 온음표
                    note.setImageResource(R.drawable.unote0f); break;
                case 'C':  // 2분음표 점
                    note.setImageResource(R.drawable.unote2pf); break;
                case 'D':  // 2분음표
                    note.setImageResource(R.drawable.unote2f); break;
                case 'E':  // 4분음표 점
                    note.setImageResource(R.drawable.unote4pf); break;
                case 'F':  // 4분음표
                    note.setImageResource(R.drawable.unote4f); break;
                case 'G':  // 8분음표 점
                    note.setImageResource(R.drawable.unote8pf); break;
                case 'H':  // 8분음표
                    note.setImageResource(R.drawable.unote8f); break;
                case 'I':  // 16분음표 점
                    note.setImageResource(R.drawable.unote16pf); break;
                case 'J':  // 16분음표
                    note.setImageResource(R.drawable.unote16f); break;
                default:    // 에러
                    note.setImageResource(R.drawable.note_error);
            }
            params_note.width = 48;
        }
    }


    // 5선지 아래도일 때 줄이 추가된 음표로
    public void addNoteBar(char noteType, ImageView note, LinearLayout.LayoutParams params_note, char option) {
        if (option == '%') {
            switch (noteType) {
                case 'A':  // 온음표 점
                    note.setImageResource(R.drawable.note0pb); break;
                case 'B':  // 온음표
                    note.setImageResource(R.drawable.note0b); break;
                case 'C':  // 2분음표 점
                    note.setImageResource(R.drawable.note2pb); break;
                case 'D':  // 2분음표
                    note.setImageResource(R.drawable.note2b); break;
                case 'E':  // 4분음표 점
                    note.setImageResource(R.drawable.note4pb); break;
                case 'F':  // 4분음표
                    note.setImageResource(R.drawable.note4b); break;
                case 'G':  // 8분음표 점
                    note.setImageResource(R.drawable.note8pb); break;
                case 'H':  // 8분음표
                    note.setImageResource(R.drawable.note8b); break;
                case 'I':  // 16분음표 점
                    note.setImageResource(R.drawable.note16pb); break;
                case 'J':  // 16분음표
                    note.setImageResource(R.drawable.note16b); break;
                default:    // 에러
                    note.setImageResource(R.drawable.note_error);
            }
        } else if (option == '#') {
            switch (noteType) {
                case 'A':  // 온음표 점
                    note.setImageResource(R.drawable.note0pbs); break;
                case 'B':  // 온음표
                    note.setImageResource(R.drawable.note0bs); break;
                case 'C':  // 2분음표 점
                    note.setImageResource(R.drawable.note2pbs); break;
                case 'D':  // 2분음표
                    note.setImageResource(R.drawable.note2bs); break;
                case 'E':  // 4분음표 점
                    note.setImageResource(R.drawable.note4pbs); break;
                case 'F':  // 4분음표
                    note.setImageResource(R.drawable.note4bs); break;
                case 'G':  // 8분음표 점
                    note.setImageResource(R.drawable.note8pbs); break;
                case 'H':  // 8분음표
                    note.setImageResource(R.drawable.note8bs); break;
                case 'I':  // 16분음표 점
                    note.setImageResource(R.drawable.note16pbs); break;
                case 'J':  // 16분음표
                    note.setImageResource(R.drawable.note16bs); break;
                default:    // 에러
                    note.setImageResource(R.drawable.note_error);
            }
            params_note.width = 48;
        } else if (option == '$') {
            switch (noteType) {
                case 'A':  // 온음표 점
                    note.setImageResource(R.drawable.note0pbf); break;
                case 'B':  // 온음표
                    note.setImageResource(R.drawable.note0bf); break;
                case 'C':  // 2분음표 점
                    note.setImageResource(R.drawable.note2pbf); break;
                case 'D':  // 2분음표
                    note.setImageResource(R.drawable.note2bf); break;
                case 'E':  // 4분음표 점
                    note.setImageResource(R.drawable.note4pbf); break;
                case 'F':  // 4분음표
                    note.setImageResource(R.drawable.note4bf); break;
                case 'G':  // 8분음표 점
                    note.setImageResource(R.drawable.note8pbf); break;
                case 'H':  // 8분음표
                    note.setImageResource(R.drawable.note8bf); break;
                case 'I':  // 16분음표 점
                    note.setImageResource(R.drawable.note16pbf); break;
                case 'J':  // 16분음표
                    note.setImageResource(R.drawable.note16bf); break;
                default:    // 에러
                    note.setImageResource(R.drawable.note_error);
            }
            params_note.width = 48;
        }
    }

    // 5선지 위에 라일 때 줄이 추가된 음표로
    public void addUpNoteBar(char noteType, ImageView note, LinearLayout.LayoutParams params_note, char option) {
        if (option == '%') {
            switch (noteType) {
                case 'A':  // 온음표 점
                    note.setImageResource(R.drawable.unote0pb); break;
                case 'B':  // 온음표
                    note.setImageResource(R.drawable.unote0b); break;
                case 'C':  // 2분음표 점
                    note.setImageResource(R.drawable.unote2pb); break;
                case 'D':  // 2분음표
                    note.setImageResource(R.drawable.unote2b); break;
                case 'E':  // 4분음표 점
                    note.setImageResource(R.drawable.unote4pb); break;
                case 'F':  // 4분음표
                    note.setImageResource(R.drawable.unote4b); break;
                case 'G':  // 8분음표 점
                    note.setImageResource(R.drawable.unote8pb); break;
                case 'H':  // 8분음표
                    note.setImageResource(R.drawable.unote8b); break;
                case 'I':  // 16분음표 점
                    note.setImageResource(R.drawable.unote16pb); break;
                case 'J':  // 16분음표
                    note.setImageResource(R.drawable.unote16b); break;
                default:    // 에러
                    note.setImageResource(R.drawable.note_error);
            }
        } else if (option == '#') {
            switch (noteType) {
                case 'A':  // 온음표 점
                    note.setImageResource(R.drawable.unote0pbs); break;
                case 'B':  // 온음표
                    note.setImageResource(R.drawable.unote0bs); break;
                case 'C':  // 2분음표 점
                    note.setImageResource(R.drawable.unote2pbs); break;
                case 'D':  // 2분음표
                    note.setImageResource(R.drawable.unote2bs); break;
                case 'E':  // 4분음표 점
                    note.setImageResource(R.drawable.unote4pbs); break;
                case 'F':  // 4분음표
                    note.setImageResource(R.drawable.unote4bs); break;
                case 'G':  // 8분음표 점
                    note.setImageResource(R.drawable.unote8pbs); break;
                case 'H':  // 8분음표
                    note.setImageResource(R.drawable.unote8bs); break;
                case 'I':  // 16분음표 점
                    note.setImageResource(R.drawable.unote16pbs); break;
                case 'J':  // 16분음표
                    note.setImageResource(R.drawable.unote16bs); break;
                default:    // 에러
                    note.setImageResource(R.drawable.note_error);
            }
            params_note.width = 48;
        } else if (option == '$') {
            switch (noteType) {
                case 'A':  // 온음표 점
                    note.setImageResource(R.drawable.unote0pbf); break;
                case 'B':  // 온음표
                    note.setImageResource(R.drawable.unote0bf); break;
                case 'C':  // 2분음표 점
                    note.setImageResource(R.drawable.unote2pbf); break;
                case 'D':  // 2분음표
                    note.setImageResource(R.drawable.unote2bf); break;
                case 'E':  // 4분음표 점
                    note.setImageResource(R.drawable.unote4pbf); break;
                case 'F':  // 4분음표
                    note.setImageResource(R.drawable.unote4bf); break;
                case 'G':  // 8분음표 점
                    note.setImageResource(R.drawable.unote8pbf); break;
                case 'H':  // 8분음표
                    note.setImageResource(R.drawable.unote8bf); break;
                case 'I':  // 16분음표 점
                    note.setImageResource(R.drawable.unote16pbf); break;
                case 'J':  // 16분음표
                    note.setImageResource(R.drawable.unote16bf); break;
                default:    // 에러
                    note.setImageResource(R.drawable.note_error);
            }
            params_note.width = 48;
        }
    }
}
