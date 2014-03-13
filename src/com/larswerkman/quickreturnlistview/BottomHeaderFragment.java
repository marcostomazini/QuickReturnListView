/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.larswerkman.quickreturnlistview;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

public class BottomHeaderFragment extends ListFragment {

	private QuickReturnListView mListView;
	//private TextView mQuickReturnView;
	private LinearLayout mQuickReturnViewTop;
	private LinearLayout mQuickReturnView;
	private int mQuickReturnHeightTop;
	private int mQuickReturnHeight;
	
	private View mPlaceHolder;
	private View mHeader;
	
	private int mCachedVerticalScrollRange;
	private static final int STATE_ONSCREEN = 0;
	private static final int STATE_OFFSCREEN = 1;
	private static final int STATE_RETURNING = 2;
	private int mState = STATE_ONSCREEN;
	private int mScrollYtop;
	private int mMinRawYtop = 0;

	private int mScrollY;
	private int mMinRawY = 0;
	
	private TranslateAnimation animTop;
	private TranslateAnimation anim;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_footer_header, null);
		mHeader = inflater.inflate(R.layout.header, null);
		//mQuickReturnView = (TextView) view.findViewById(R.id.sticky);
				
		mQuickReturnViewTop = (LinearLayout) view.findViewById(R.id.comanda_layout);
		mQuickReturnView = (LinearLayout) view.findViewById(R.id.footer1);
		
		mPlaceHolder = mHeader.findViewById(R.id.placeholder);
		
		Button btn1 = (Button) view.findViewById(R.id.button1);
		btn1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Toast toast = Toast.makeText(getActivity(), "teste 1 btn",
						Toast.LENGTH_SHORT);
				toast.show();				
			}
		});
		
		Button btn2 = (Button) view.findViewById(R.id.button2);
		btn2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Toast toast = Toast.makeText(getActivity(), "teste 2",
						Toast.LENGTH_SHORT);
				toast.show();				
			}
		});
		
		Button btn3 = (Button) view.findViewById(R.id.button3);
		btn3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Toast toast = Toast.makeText(getActivity(), "teste 3 rodapeeee",
						Toast.LENGTH_SHORT);
				toast.show();				
			}
		});
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mListView = (QuickReturnListView) getListView();
		
		//mQuickReturnView.setText("Default");
		//mListView.addHeaderView(mHeader);
		
		String[] array = new String[] { "Android", "Android", "Android",
				"Android", "Android", "Android", "Android", "Android",
				"Android", "Android", "Android", "Android", "Android",
				"Android", "Android", "Android" };
		
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				R.layout.list_item,
				R.id.text1, array));
		
		mListView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mQuickReturnHeightTop = mQuickReturnViewTop.getHeight();
						mQuickReturnHeight = mQuickReturnView.getHeight();
						mListView.computeScrollY();
						mCachedVerticalScrollRange = mListView.getListHeight();
					}
				});

		mListView.setOnScrollListener(new OnScrollListener() {
			@SuppressLint("NewApi")
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
				int translationYtop = topAnim();							
				int translationY = bottonAnim();
				
				/** this can be used if the build is below honeycomb **/
				if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
					animTop = new TranslateAnimation(0, 0, translationYtop,
							translationYtop);
					animTop.setFillAfter(true);
					animTop.setDuration(0);
					mQuickReturnViewTop.startAnimation(animTop);
					mQuickReturnView.startAnimation(anim);
				} else {
					mQuickReturnViewTop.setTranslationY(translationYtop);
					mQuickReturnView.setTranslationY(translationY);
				}

			}

			private int topAnim() {
				mScrollYtop = 0;
				int translationYtop = 0;
				
				if (mListView.scrollYIsComputed()) {
					mScrollYtop = mListView.getComputedScrollY();
				}

				int rawYtop = mPlaceHolder.getTop()
						- Math.min(
								mCachedVerticalScrollRange
										- mListView.getHeight(), mScrollYtop);
				
				switch (mState) {
				case STATE_OFFSCREEN:
					if (rawYtop <= mMinRawYtop) {
						mMinRawYtop = rawYtop;
					} else {
						mState = STATE_RETURNING;
					}
					translationYtop = rawYtop;
					break;

				case STATE_ONSCREEN:
					if (rawYtop < -mQuickReturnHeightTop) {
						mState = STATE_OFFSCREEN;
						mMinRawYtop = rawYtop;
					}
					translationYtop = rawYtop;
					break;

				case STATE_RETURNING:
					translationYtop = (rawYtop - mMinRawYtop) - mQuickReturnHeightTop;
					if (translationYtop > 0) {
						translationYtop = 0;
						mMinRawYtop = rawYtop - mQuickReturnHeightTop;
					}

					if (rawYtop > 0) {
						mState = STATE_ONSCREEN;
						translationYtop = rawYtop;
					}

					if (translationYtop < -mQuickReturnHeightTop) {
						mState = STATE_OFFSCREEN;
						mMinRawYtop = rawYtop;
					}
					break;
				}
				return translationYtop;
			}

			private int bottonAnim() {
				// bottom
				mScrollY = 0;
				int translationY = 0;

				if (mListView.scrollYIsComputed()) {
					mScrollY = mListView.getComputedScrollY();
				}

				int rawY = mScrollY;

				switch (mState) {
				case STATE_OFFSCREEN:
					if (rawY >= mMinRawY) {
						mMinRawY = rawY;
					} else {
						mState = STATE_RETURNING;
					}
					translationY = rawY;
					break;

				case STATE_ONSCREEN:
					if (rawY > mQuickReturnHeight) {
						mState = STATE_OFFSCREEN;
						mMinRawY = rawY;
					}
					translationY = rawY;
					break;

				case STATE_RETURNING:

					translationY = (rawY - mMinRawY) + mQuickReturnHeight;

					System.out.println(translationY);
					if (translationY < 0) {
						translationY = 0;
						mMinRawY = rawY + mQuickReturnHeight;
					}

					if (rawY == 0) {
						mState = STATE_ONSCREEN;
						translationY = 0;
					}

					if (translationY > mQuickReturnHeight) {
						mState = STATE_OFFSCREEN;
						mMinRawY = rawY;
					}
					break;
				}
				return translationY;
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
		});
	}
}