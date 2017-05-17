package com.example.borntodieee.zhiwuya.detail;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.borntodieee.zhiwuya.R;

public class DetailActivity extends AppCompatActivity {

    private DetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.frame);

        if (savedInstanceState != null) {
            detailFragment = (DetailFragment) getSupportFragmentManager().getFragment(savedInstanceState, "detailFragment");
        } else {
            detailFragment = new DetailFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, detailFragment).commit();
        }

        DetailPresenter presenter = new DetailPresenter(DetailActivity.this, detailFragment);
        Intent intent = getIntent();
        presenter.setId(intent.getIntExtra("id", 0));
        presenter.setTitle(intent.getStringExtra("title"));
        presenter.setCoverUrl(intent.getStringExtra("coverUrl"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (detailFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "detailFragment", detailFragment);        }
    }
}
