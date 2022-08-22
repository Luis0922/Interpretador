package interpreter.expr;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import interpreter.value.MapValue;
import interpreter.value.Value;

public class MapExpr extends Expr{
	
	private List<MapItem> array = new ArrayList<MapItem>();
	
	public MapExpr(int line) {
		super(line);
	}
	
	@Override
	public Value<?> expr() {
		Map<String, Value<?>> map = new HashMap<>();
		for(int i=0; i < array.size(); i++) {
			map.put(array.get(i).Key, array.get(i).value.expr());
		}
		MapValue v = new MapValue(map);
		return v;
	}

	public void addItem(MapItem item) {
		array.add(item);
	}
}
