package com.myylook.game.util;

import com.alibaba.fastjson.JSONObject;
import com.myylook.common.utils.ToastUtil;
import com.myylook.common.utils.WordUtil;
import com.myylook.game.GameConsts;
import com.myylook.game.R;
import com.myylook.game.bean.BankerBean;
import com.myylook.game.bean.GameParam;
import com.myylook.game.interfaces.GameActionListener;
import com.myylook.game.views.AbsGameViewHolder;
import com.myylook.game.views.GameEbbViewHolder;
import com.myylook.game.views.GameHdViewHolder;
import com.myylook.game.views.GameNzViewHolder;
import com.myylook.game.views.GameZjhViewHolder;
import com.myylook.game.views.GameZpViewHolder;

import java.util.List;


/**
 * Created by cxf on 2018/10/31.
 */

public class GamePresenter {
    private GameParam mGameParam;
    private List<Integer> mGameList;
    private AbsGameViewHolder mGameViewHolder;
    private GameSoundPool mGameSoundPool;
    private boolean mEnd;
    private BankerBean mBankerBean;
    private String mBankerLimitString;
    private GameActionListener mGameActionListener;

    public GamePresenter() {
        mGameSoundPool = new GameSoundPool();
    }

    public GamePresenter(GameParam param) {
        this();
        setGameParam(param);
    }

    public void setGameParam(GameParam param) {
        mGameParam = param;
        mGameActionListener=param.getGameActionListener();
        boolean anchor = param.isAnchor();
        JSONObject obj = param.getObj();
        mBankerBean = new BankerBean(
                obj.getString("game_bankerid"),
                obj.getString("game_banker_name"),
                obj.getString("game_banker_avatar"),
                obj.getString("game_banker_coin"));
        mBankerLimitString = WordUtil.getString(R.string.game_nz_apply_sz_yajin_zd) + obj.getString("game_banker_limit") + param.getCoinName();
        if (!anchor) {
            int gameAction = obj.getIntValue("gameaction");
            int betTime = obj.getIntValue("gametime");
            int[] totalBet = obj.getObject("game", int[].class);
            int[] myBet = obj.getObject("gamebet", int[].class);
            if (gameAction != 0 && betTime > 0 && totalBet.length > 0 && myBet.length > 0) {
                createGameViewHolder(gameAction);
                if (mGameViewHolder != null) {
                    mGameViewHolder.setGameID(obj.getString("gameid"));
                    mGameViewHolder.setBetTime(betTime);
                    mGameViewHolder.setTotalBet(totalBet);
                    mGameViewHolder.setMyBet(myBet);
                    mGameViewHolder.enterRoomOpenGameWindow();
                }
            }
        }
    }

    public void setGameList(List<Integer> gameList) {
        mGameList = gameList;
    }

    public List<Integer> getGameList() {
        return mGameList;
    }

    /**
     * ????????????
     */
    public void startGame(int gameAction) {
        if (mGameActionListener == null) {
            return;
        }
        if (mGameActionListener.isLinkMicIng()) {
            ToastUtil.show(R.string.live_link_mic_cannot_game);
            return;
        }
        if (mGameViewHolder != null && mGameViewHolder.isBetStarted()) {
            ToastUtil.show(R.string.game_wait_end);
            return;
        }
        mGameActionListener.showGameWindow(gameAction);
    }

    /**
     * ????????????
     */
    public void closeGame() {
        if (mGameViewHolder != null) {
            mGameViewHolder.anchorCloseGame();
        }
    }

    private void createGameViewHolder(int gameAction) {
        AbsGameViewHolder gameViewHolder = null;
        switch (gameAction) {
            case GameConsts.GAME_ACTION_ZJH:
                gameViewHolder = new GameZjhViewHolder(mGameParam, mGameSoundPool);
                break;
            case GameConsts.GAME_ACTION_HD:
                gameViewHolder = new GameHdViewHolder(mGameParam, mGameSoundPool);
                break;
            case GameConsts.GAME_ACTION_ZP:
                gameViewHolder = new GameZpViewHolder(mGameParam, mGameSoundPool);
                break;
            case GameConsts.GAME_ACTION_NZ:
                if (mBankerBean != null) {
                    gameViewHolder = new GameNzViewHolder(mGameParam, mGameSoundPool, mBankerBean, mBankerLimitString);
                }
                break;
            case GameConsts.GAME_ACTION_EBB:
                gameViewHolder = new GameEbbViewHolder(mGameParam, mGameSoundPool);
                break;
        }
        if (gameViewHolder != null) {
            mGameViewHolder = gameViewHolder;
            if (mGameActionListener != null) {
                mGameActionListener.onGamePlayChanged(true);
            }
        }
    }


    /**
     * ?????? ???????????? socket??????
     */
    public void onGameZjhSocket(JSONObject obj) {
        if (mEnd) {
            return;
        }
        int action = obj.getIntValue("action");
        if (action == GameConsts.GAME_ACTION_OPEN_WINDOW) {
            if (mGameViewHolder != null) {
                mGameViewHolder.removeFromParent();
                mGameViewHolder.release();
                mGameViewHolder = null;
            }
            createGameViewHolder(GameConsts.GAME_ACTION_ZJH);
        } else if (action == GameConsts.GAME_ACTION_CREATE) {
            if (mGameViewHolder != null) {
                if (!(mGameViewHolder instanceof GameZjhViewHolder)) {
                    mGameViewHolder.removeFromParent();
                    mGameViewHolder.release();
                    mGameViewHolder = null;
                    createGameViewHolder(GameConsts.GAME_ACTION_ZJH);
                }
            } else {
                createGameViewHolder(GameConsts.GAME_ACTION_ZJH);
            }
        }
        if (mGameViewHolder != null && mGameViewHolder instanceof GameZjhViewHolder) {
            mGameViewHolder.handleSocket(action, obj);
        }
        if (action == GameConsts.GAME_ACTION_CLOSE) {
            mGameViewHolder = null;
            if (mGameActionListener != null) {
                mGameActionListener.onGamePlayChanged(false);
            }
        }
    }

    /**
     * ?????? ???????????? socket??????
     */
    public void onGameHdSocket(JSONObject obj) {
        if (mEnd) {
            return;
        }
        int action = obj.getIntValue("action");
        if (action == GameConsts.GAME_ACTION_OPEN_WINDOW) {
            if (mGameViewHolder != null) {
                mGameViewHolder.removeFromParent();
                mGameViewHolder.release();
                mGameViewHolder = null;
            }
            createGameViewHolder(GameConsts.GAME_ACTION_HD);
        } else if (action == GameConsts.GAME_ACTION_CREATE) {
            if (mGameViewHolder != null) {
                if (!(mGameViewHolder instanceof GameHdViewHolder)) {
                    mGameViewHolder.removeFromParent();
                    mGameViewHolder.release();
                    mGameViewHolder = null;
                    createGameViewHolder(GameConsts.GAME_ACTION_HD);
                }
            } else {
                createGameViewHolder(GameConsts.GAME_ACTION_HD);
            }
        }
        if (mGameViewHolder != null && mGameViewHolder instanceof GameHdViewHolder) {
            mGameViewHolder.handleSocket(action, obj);
        }
        if (action == GameConsts.GAME_ACTION_CLOSE) {
            mGameViewHolder = null;
            if (mGameActionListener != null) {
                mGameActionListener.onGamePlayChanged(false);
            }
        }
    }

    /**
     * ?????? ???????????? socket??????
     */
    public void onGameZpSocket(JSONObject obj) {
        if (mEnd) {
            return;
        }
        int action = obj.getIntValue("action");
        if (action == GameConsts.GAME_ACTION_OPEN_WINDOW) {
            if (mGameViewHolder != null) {
                mGameViewHolder.removeFromParent();
                mGameViewHolder.release();
                mGameViewHolder = null;
            }
            createGameViewHolder(GameConsts.GAME_ACTION_ZP);
        } else if (action == GameConsts.GAME_ACTION_NOTIFY_BET) {
            if (mGameViewHolder != null) {
                if (!(mGameViewHolder instanceof GameZpViewHolder)) {
                    mGameViewHolder.removeFromParent();
                    mGameViewHolder.release();
                    mGameViewHolder = null;
                    createGameViewHolder(GameConsts.GAME_ACTION_ZP);
                }
            } else {
                createGameViewHolder(GameConsts.GAME_ACTION_ZP);
            }
        }
        if (mGameViewHolder != null && mGameViewHolder instanceof GameZpViewHolder) {
            mGameViewHolder.handleSocket(action, obj);
        }
        if (action == GameConsts.GAME_ACTION_CLOSE) {
            mGameViewHolder = null;
            if (mGameActionListener != null) {
                mGameActionListener.onGamePlayChanged(false);
            }
        }
    }

    /**
     * ?????? ???????????? socket??????
     */
    public void onGameNzSocket(JSONObject obj) {
        if (mEnd) {
            return;
        }
        int action = obj.getIntValue("action");
        if (action == GameConsts.GAME_ACTION_OPEN_WINDOW) {
            if (mGameViewHolder != null) {
                mGameViewHolder.removeFromParent();
                mGameViewHolder.release();
                mGameViewHolder = null;
            }
            createGameViewHolder(GameConsts.GAME_ACTION_NZ);
        } else if (action == GameConsts.GAME_ACTION_CREATE) {
            if (mGameViewHolder != null) {
                if (!(mGameViewHolder instanceof GameNzViewHolder)) {
                    mGameViewHolder.removeFromParent();
                    mGameViewHolder.release();
                    mGameViewHolder = null;
                    createGameViewHolder(GameConsts.GAME_ACTION_NZ);
                }
            } else {
                createGameViewHolder(GameConsts.GAME_ACTION_NZ);
            }
        }
        if (mGameViewHolder != null && mGameViewHolder instanceof GameNzViewHolder) {
            mGameViewHolder.handleSocket(action, obj);
        }
        if (action == GameConsts.GAME_ACTION_CLOSE) {
            mGameViewHolder = null;
            if (mGameActionListener != null) {
                mGameActionListener.onGamePlayChanged(false);
            }
        }
    }

    /**
     * ?????? ????????? socket??????
     */
    public void onGameEbbSocket(JSONObject obj) {
        if (mEnd) {
            return;
        }
        int action = obj.getIntValue("action");
        if (action == GameConsts.GAME_ACTION_OPEN_WINDOW) {
            if (mGameViewHolder != null) {
                mGameViewHolder.removeFromParent();
                mGameViewHolder.release();
                mGameViewHolder = null;
            }
            createGameViewHolder(GameConsts.GAME_ACTION_EBB);
        } else if (action == GameConsts.GAME_ACTION_CREATE) {
            if (mGameViewHolder != null) {
                if (!(mGameViewHolder instanceof GameEbbViewHolder)) {
                    mGameViewHolder.removeFromParent();
                    mGameViewHolder.release();
                    mGameViewHolder = null;
                    createGameViewHolder(GameConsts.GAME_ACTION_EBB);
                }
            } else {
                createGameViewHolder(GameConsts.GAME_ACTION_EBB);
            }
        }
        if (mGameViewHolder != null && mGameViewHolder instanceof GameEbbViewHolder) {
            mGameViewHolder.handleSocket(action, obj);
        }
        if (action == GameConsts.GAME_ACTION_CLOSE) {
            mGameViewHolder = null;
            if (mGameActionListener != null) {
                mGameActionListener.onGamePlayChanged(false);
            }
        }
    }

    public void setLastCoin(String coin) {
        if (mGameViewHolder != null) {
            mGameViewHolder.setLastCoin(coin);
        }
    }

    public void clearGame() {
        if (mGameViewHolder != null) {
            mGameViewHolder.hideGameWindow();
            mGameViewHolder.release();
        }
        mGameViewHolder = null;
        if (mGameActionListener != null) {
            mGameActionListener.onGamePlayChanged(false);
        }
    }


    public void release() {
        mEnd = true;
        if (mGameSoundPool != null) {
            mGameSoundPool.release();
        }
        if (mGameActionListener != null) {
            mGameActionListener.onGamePlayChanged(false);
            mGameActionListener.release();
        }
        mGameActionListener = null;
        mGameList = null;
        if (mGameViewHolder != null) {
            mGameViewHolder.release();
        }
        mGameViewHolder = null;
    }

}
