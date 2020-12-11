package com.martin.reciper.ui.converter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.martin.reciper.R;
import com.martin.reciper.models.Units;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Objects;

@SuppressWarnings("PointlessBooleanExpression")
public class ConverterFragment extends Fragment
{
    private ConverterViewModel converterViewModel;
    SharedPreferences settings;
    DecimalFormat formatter = new DecimalFormat("#.###");

    enum UnitScope {WEIGHT, VOLUME};

    EditText edit_grams, edit_decagrams, edit_cups, edit_ounces,
            edit_mililiters, edit_pints, edit_tablespoons, edit_teaspoons;
    EditText edit_portions;
    TextView text_convertResult1, text_convertResult2;
    CheckBox check_portions;

    int favWeightId;
    int favVolumeId;
    float baseValue = 0; //grams
    float basePortionedValue = 0; //for portions
    float portions = 1;
    private boolean _ignore = false;
    UnitScope scope;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        converterViewModel = new ViewModelProvider(requireActivity()).get(ConverterViewModel.class);

        settings = PreferenceManager.getDefaultSharedPreferences(requireContext());
        favWeightId = Integer.parseInt(settings.getString("fav_weight_unit", "3"));
        favVolumeId = Integer.parseInt(settings.getString("fav_volume_unit", "3"));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_converter, container, false);

        edit_grams = view.findViewById(R.id.edit_grams);
            edit_grams.addTextChangedListener(new cWatcher(edit_grams));
        edit_decagrams  = view.findViewById(R.id.edit_decagrams);
            edit_decagrams.addTextChangedListener(new cWatcher(edit_decagrams));
        edit_cups = view.findViewById(R.id.edit_cups);
            edit_cups.addTextChangedListener(new cWatcher(edit_cups));
        edit_ounces = view.findViewById(R.id.edit_ounces);
            edit_ounces.addTextChangedListener(new cWatcher(edit_ounces));
        edit_mililiters = view.findViewById(R.id.edit_milliliters);
            edit_mililiters.addTextChangedListener(new cWatcher(edit_mililiters));
        edit_pints = view.findViewById(R.id.edit_pints);
            edit_pints.addTextChangedListener(new cWatcher(edit_pints));
        edit_tablespoons = view.findViewById(R.id.edit_tablespoons);
            edit_tablespoons.addTextChangedListener(new cWatcher(edit_tablespoons));
        edit_teaspoons = view.findViewById(R.id.edit_teaspoons);
            edit_teaspoons.addTextChangedListener(new cWatcher(edit_teaspoons));

        edit_portions = view.findViewById(R.id.edit_portions);
            edit_portions.setHint(settings.getString("default_portions", "1/4"));
            edit_portions.addTextChangedListener(portionsWatcher);
        text_convertResult1 = view.findViewById(R.id.text_convertResult1);
            text_convertResult1.setText("= 0");
        text_convertResult2 = view.findViewById(R.id.text_convertResult2);
            text_convertResult2.setText("= 0");
            text_convertResult2.setVisibility(View.INVISIBLE);
        check_portions = view.findViewById(R.id.check_portions);
            check_portions.setOnCheckedChangeListener(onPortionsCheckedChangeListener);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        Bundle stateBundle = converterViewModel.getStateBundle();
        if(stateBundle != null)
        {
            Log.i("daco",stateBundle.toString());
            if(!stateBundle.getString("WeightString").isEmpty())
                edit_grams.setText(stateBundle.getString("WeightString"));
            if(!stateBundle.getString("VolumeString").isEmpty())
                edit_mililiters.setText(stateBundle.getString("VolumeString"));
            edit_portions.setText(stateBundle.getString("PortionsString"));
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        Bundle stateBundle = new Bundle();
        stateBundle.putString("WeightString", edit_grams.getText().toString());
        stateBundle.putString("VolumeString", edit_mililiters.getText().toString());
        stateBundle.putString("PortionsString", edit_portions.getText().toString());

        converterViewModel.setStateBundle(stateBundle);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_converter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.menu_converter_clear)
            clearFields();
        return true;
    }

    TextWatcher portionsWatcher = new TextWatcher()  //portions value changed listener
    {
        @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override public void afterTextChanged(Editable editable)
        {
            String ratioString = editable.toString();
            try //parse
            {
                if(ratioString.isEmpty())
                    throw new ParseException("empty", 0);

                String[] ratioNums = ratioString.split("/");
                if(ratioNums.length == 2)
                    portions = Float.parseFloat(ratioNums[0]) / Float.parseFloat(ratioNums[1]);
                else
                    portions = Float.parseFloat(ratioString); //throws NumberFormatException
            }
            catch(ParseException | NumberFormatException e) //parsing unsuccessful
            {
                check_portions.setChecked(false);
                return;
            }

            if(check_portions.isChecked() == false) //vyplnena hodnota bez zaskrtnuteho policka
                check_portions.setChecked(true);

            rewriteResult();
        }
    };

    protected void rewriteResult()
    {
        if(scope == null) return; //it is in initial state

        String unitName = "";
        float ratio = 0;
        if (scope == UnitScope.WEIGHT)
        {
            unitName = Objects.requireNonNull(Units.getById(favWeightId)).getName();
            ratio = Objects.requireNonNull(Units.getById(favWeightId)).getRatio();
        }
        if (scope == UnitScope.VOLUME)
        {
            unitName = Objects.requireNonNull(Units.getById(favVolumeId)).getName();
            ratio = Objects.requireNonNull(Units.getById(favVolumeId)).getRatio();
        }

        basePortionedValue = baseValue * portions;
        text_convertResult1.setText(String.format("= %s %s", formatter.format(baseValue * 1/ratio), unitName));
        text_convertResult2.setText(String.format("= %s %s", formatter.format(basePortionedValue * 1/ratio), unitName));
    }

    public void rewriteResult(UnitScope s)
    {
        scope = s;
        rewriteResult();
    }

    protected void clearFields()
    {
        clearFields(UnitScope.WEIGHT);
        clearFields(UnitScope.VOLUME);
    }

    protected void clearFields(UnitScope s)
    {
        if(s == UnitScope.WEIGHT)
            {edit_grams.setText(null); edit_decagrams.setText(null); edit_cups.setText(null); edit_ounces.setText(null);}
        if(s == UnitScope.VOLUME)
            {edit_mililiters.setText(null); edit_pints.setText(null); edit_tablespoons.setText(null); edit_teaspoons.setText(null);}
    }

    CompoundButton.OnCheckedChangeListener onPortionsCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b)
        {
            boolean checkedState = check_portions.isChecked();
            text_convertResult2.setVisibility(checkedState ? View.VISIBLE : View.INVISIBLE);
            if(checkedState == true && edit_portions.getText().toString().isEmpty())
                edit_portions.setText(settings.getString("default_portions","1/4"));
            if(checkedState == false)
                edit_portions.setText(new String());

        }
    };

    class cWatcher implements TextWatcher
    {
        private final EditText field;

        public cWatcher(EditText editText) {field = editText;}

        @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override public void afterTextChanged(Editable editable)
        {
            if (_ignore) return;
            try //parse modified field
            {
                baseValue = Float.parseFloat(field.getText().toString());
            }
            catch (NumberFormatException e) //empty value
            {
                _ignore = true;
                clearFields(UnitScope.WEIGHT); clearFields(UnitScope.VOLUME);
                text_convertResult1.setText("= 0"); text_convertResult2.setText("= 0");
                _ignore = false;
                return;
            }

            if (field == edit_grams) baseValue *= Units.grams.getRatio();
            if (field == edit_decagrams) baseValue *= Units.decagrams.getRatio();
            if (field == edit_cups) baseValue *= Units.cups.getRatio();
            if (field == edit_ounces) baseValue *= Units.ounces.getRatio();
            if (field == edit_mililiters) baseValue *= Units.mililiters.getRatio();
            if (field == edit_pints) baseValue *= Units.pints.getRatio();
            if (field == edit_tablespoons) baseValue *= Units.tablespoons.getRatio();
            if (field == edit_teaspoons) baseValue *= Units.teaspoons.getRatio();

            _ignore = true;
            if (field == edit_grams || field == edit_decagrams || field == edit_cups || field == edit_ounces)
            {
                clearFields(UnitScope.VOLUME);
                if (field != edit_grams) edit_grams.setText(formatter.format(baseValue * 1/Units.grams.getRatio()));
                if (field != edit_decagrams) edit_decagrams.setText(formatter.format(baseValue * 1/Units.decagrams.getRatio()));
                if (field != edit_cups) edit_cups.setText(formatter.format(baseValue * 1/Units.cups.getRatio()));
                if (field != edit_ounces) edit_ounces.setText(formatter.format(baseValue * 1/Units.ounces.getRatio()));
                rewriteResult(UnitScope.WEIGHT);
            }
            if (field == edit_mililiters || field == edit_pints || field == edit_tablespoons || field == edit_teaspoons)
            {
                clearFields(UnitScope.WEIGHT);
                if (field != edit_mililiters) edit_mililiters.setText(formatter.format(baseValue * 1/Units.mililiters.getRatio()));
                if (field != edit_pints) edit_pints.setText(formatter.format(baseValue * 1/Units.pints.getRatio()));
                if (field != edit_tablespoons) edit_tablespoons.setText(formatter.format(baseValue * 1/Units.tablespoons.getRatio()));
                if (field != edit_teaspoons) edit_teaspoons.setText(formatter.format(baseValue * 1/Units.teaspoons.getRatio()));
                rewriteResult(UnitScope.VOLUME);
            }
            _ignore = false;
        }
    } //koniec cWatchera
}