type Ref;
const null: Ref;

var val: [Ref]int;
var next: [Ref]Ref;
var packedRange: [Ref] bool;
var fracRange: [Ref] real;
var packedUniRange: [Ref] bool;
var fracUniRange: [Ref] real;

procedure ConstructLink(val1 :int, next1 : Ref, this: Ref);
	 ensures (val[this] == val1) &&
 	 	 (next[this] == next1); 
 
procedure PackRange(x:int, y:int, this:Ref);
	 requires packedRange[this]==false &&
	 	((((val[this]>=x))&&(val[this]<=y))&&((fracRange[next[this]] > 0.0)||(next[this]==null)));

procedure UnpackRange(x:int, y:int, this:Ref);
	 requires packedRange[this] &&
	 	 (fracRange[this] > 0.0);
	 ensures ((((val[this]>=x))&&(val[this]<=y))&&((fracRange[next[this]] > 0.0)||(next[this]==null)));

procedure PackUniRange(x:int, y:int, this:Ref);
	 requires packedUniRange[this]==false &&
	 	((((val[this]>=x))&&(val[this]<=y))&&(fracUniRange[next[this]] == 1.0||(next[this]==null)));

procedure UnpackUniRange(x:int, y:int, this:Ref);
	 requires packedUniRange[this] &&
	 	 (fracUniRange[this] > 0.0);
	 ensures ((((val[this]>=x))&&(val[this]<=y))&&(fracUniRange[next[this]] == 1.0||(next[this]==null)));

procedure addModulo11(x:int,this:Ref)
	 modifies fracRange,next,packedRange,val;
	 requires ((x>=0)&&packedRange[this] && 
 	 	(fracRange[this] > 0.0));
	 ensures packedRange[this] && 
 	 	(fracRange[this] > 0.0);
requires (forall x0:Ref :: packedRange[x0]);
	 ensures (forall x0:Ref:: (packedRange[x0] == old(packedRange[x0])));
	 ensures (forall x0:Ref:: (fracRange[x0] == old(fracRange[x0])));

{
call UnpackRange(0, 10, this);
packedRange[0, 10, this] := false;
val[this]:=modulo(val[this]+x,11);
call PackRange(0, 10, this);
packedRange[0, 10, this] := true;
if (next[this]!=null)
{
	 call addModulo11(x,next[this]);
	fracRange[next[this]] := fracRange[next[this]] / 2.0;
	fracRange[next[this]] := fracRange[next[this]] * 2.0;
}
 }
 function modulo(x:int, y:int) returns (int); 
axiom (forall x:int, y:int :: {modulo(x,y)}
	 ((0 <= x) &&(0 < y) ==> (0 <= modulo(x,y) ) && (modulo(x,y) < y) )
	&&
	((0 <= x) &&(y < 0) ==> (0 <= modulo(x,y) ) && (modulo(x,y) < -y) )
	&&
	((x <= 0) &&(0 < y) ==> (-y <= modulo(x,y) ) && (modulo(x,y) <= 0) )
	&&
	((x <= 0) &&(y < 0) ==> (y <= modulo(x,y) ) && (modulo(x,y) <= 0) )
	);

procedure add(z:int,x:int,y:int,this:Ref)
	 modifies fracUniRange,next,packedUniRange,val;
	 requires ((x<y)&&packedUniRange[this] && 
 	 	(fracUniRange[this] == 1.0));
	 ensures packedUniRange[this] && 
 	 	(fracUniRange[this] == 1.0);
{
call UnpackUniRange(x, y, this);
packedUniRange[x, y, this] := false;
val[this]:=val[this]+z;
if (next[this]!=null)
{
	 call add(z,x,y,next[this]);
	fracUniRange[next[this]] := fracUniRange[next[this]] / 2.0;
	fracUniRange[next[this]] := fracUniRange[next[this]] * 2.0;
}
 call PackUniRange(x+z, y+z, this);
packedUniRange[x+z, y+z, this] := true;
}
 procedure main(this:Ref)
	 modifies fracRange,next,packedRange,val;
requires (forall x:Ref :: packedRange[x]);
{
	 var l1:Ref;
	 var l2:Ref;
	 var l3:Ref;
	 call ConstructLink(3,null,l1);
packedRange[0,10,l1] := true;
fracRange[0,10,l1] := 1.0;
	 call ConstructLink(4,l1,l2);
packedRange[0,10,l2] := true;
fracRange[0,10,l2] := 1.0;
	fracRange[l1] := fracRange[l1] / 2.0;
	 call ConstructLink(5,l2,l3);
packedRange[0,10,l3] := true;
fracRange[0,10,l3] := 1.0;
	fracRange[l2] := fracRange[l2] / 2.0;
	 call addModulo11(20,l3);
	fracRange[l3] := fracRange[l3] / 2.0;
	fracRange[l3] := fracRange[l3] * 2.0;
}
 