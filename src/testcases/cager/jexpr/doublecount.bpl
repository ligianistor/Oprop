type Ref;
const null: Ref;

var val: [Ref]int;
var dbl: [Ref]int;
var packedOK: [Ref] bool;
var fracOK: [Ref] real;

procedure ConstructDoubleCount(val1 :int, dbl1 :int, this: Ref);
	 ensures (val[this] == val1) &&
 	 	 (dbl[this] == dbl1); 
 
procedure PackOK(v:int, d:int, this:Ref);
	 requires (packedOK[this]==false) &&
	 	((d==(2*v))) && (val[this]==v) && (dbl[this]==d); 
 
procedure UnpackOK(v:int, d:int, this:Ref);
	 requires packedOK[this] &&
	 	 (fracOK[this] > 0.0);
	 ensures ((d==(2*v))) && (val[this]==v) && (dbl[this]==d);


procedure increment(this:Ref)
	 modifies dbl,packedOK,val;
	 requires (this != null) && ((packedOK[this] ) && 
 	 	(fracOK[this] > 0.0));
	 ensures ((packedOK[this] ) && 
 	 	(fracOK[this] > 0.0));
	 requires (forall x:Ref :: packedOK[x]);
	 ensures (forall x:Ref :: packedOK[x]);
	 ensures (forall x:Ref :: (fracOK[x]==old(fracOK[x])));

{
	 assume (forall y:Ref :: (fracOK[y] >= 0.0) );
call UnpackOK(val[this], dbl[this], this);
packedOK[this] := false;
val[this]:=val[this]+1;
dbl[this]:=dbl[this]+2;
call PackOK(val[this], dbl[this], this);
packedOK[this] := true;
}
 