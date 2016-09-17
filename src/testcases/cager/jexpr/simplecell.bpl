type Ref;
const null: Ref;

var val: [Ref]int;
var next: [Ref]Ref;
var packedPredNext: [Ref] bool;
var fracPredNext: [Ref] real;
var packedPredVal: [Ref] bool;
var fracPredVal: [Ref] real;

procedure ConstructSimpleCell(val1 :int, next1 : Ref, this: Ref);
	 ensures (val[this] == val1) &&
 	 	 (next[this] == next1); 
 
procedure PackPredNext(obj: Ref, this:Ref);
	 requires (packedPredNext[this]==false) &&
	 	(((fracPredVal[next[this]] >= 0.34))) && (next[this]==obj); 
 
procedure UnpackPredNext(obj: Ref, this:Ref);
	 requires packedPredNext[this] &&
	 	 (fracPredNext[this] > 0.0);
	 ensures (((fracPredVal[next[this]] >= 0.34))) && (next[this]==obj);

procedure PackPredVal(v:int, this:Ref);
	 requires (packedPredVal[this]==false) &&
	 	((v<15)) && (val[this]==v); 
 
procedure UnpackPredVal(v:int, this:Ref);
	 requires packedPredVal[this] &&
	 	 (fracPredVal[this] > 0.0);
	 ensures ((v<15)) && (val[this]==v);


procedure changeVal(r:int, this:Ref)
	 modifies packedPredVal,val;
	 requires (this != null) && (((packedPredVal[this]) && 
 	 	(fracPredVal[this] >= 1.0))&&(r<15));
	 ensures ((packedPredVal[this]) && 
 	 	(fracPredVal[this] >= 1.0));
	 requires (forall x:Ref :: packedPredVal[x]);
	 ensures (forall x:Ref :: packedPredVal[x]);
	 ensures (forall x:Ref :: (fracPredVal[x]==old(fracPredVal[x])));

{
	 assume (forall y:Ref :: (fracPredVal[y] >= 0.0) );
call UnpackPredVal(val[this], this);
packedPredVal[this] := false;
val[this]:=r;
call PackPredVal(r, this);
packedPredVal[this] := true;
}
 