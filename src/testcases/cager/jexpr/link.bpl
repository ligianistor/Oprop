type Ref;
const null: Ref;

var val: [Ref]int;
var next: [Ref]Ref;
var packedRange: [int, int, Ref] bool;
var fracRange: [int, int, Ref] real;

procedure ConstructLink(val1 :int, next1 : Ref, this: Ref);
	 ensures (val[this] == val1) &&
 	 	 (next[this] == next1); 
 
procedure PackRange(x:int, y:int, this:Ref);
	 requires packedRange[x, y, this]==false &&
	 	((((val[this]>=x))&&(val[this]<=y))&&((fracRange[x,y,next[this]] > 0.0)||(next[this]==null)));

procedure UnpackRange(x:int, y:int, this:Ref);
	 requires packedRange[x, y, this] &&
	 	 (fracRange[x, y, this] > 0.0);
	 ensures ((((val[this]>=x))&&(val[this]<=y))&&((fracRange[x,y,next[this]] > 0.0)||(next[this]==null)));

procedure addModulo11(x:int,this:Ref)
	 modifies fracRange,next,packedRange,val;
	 requires ((x>=0)&&packedRange[0,10,this] && 
 	 	(fracRange[0,10,this] > 0.0));
	 ensures packedRange[0,10,this] && 
 	 	(fracRange[0,10,this] > 0.0);
requires (forall x497:int, y497:int, x0:Ref :: packedRange[x497, y497, x0]);
	 ensures (forall x497:int, y497:int, x0:Ref:: (packedRange[x497, y497, x0] == old(packedRange[x497, y497, x0])));
	 ensures (forall x497:int, y497:int, x0:Ref:: (fracRange[x497, y497, x0] == old(fracRange[x497, y497, x0])));

{
call UnpackRange(0, 10, this);
packedRange[0, 10, this] := false;
val[this]:=modulo(val[this]+x,11);
call PackRange(0, 10, this);
packedRange[0, 10, this] := true;
if (next[this]!=null)
{
	 call addModulo11(x,next[this]);
	fracRange[0,10,next[this]] := fracRange[0,10,next[this]] / 2.0;
	fracRange[0,10,next[this]] := fracRange[0,10,next[this]] * 2.0;
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

procedure main(this:Ref)
	 modifies fracRange,next,packedRange,val;
requires (forall x497:int, y497:int, x:Ref :: packedRange[x497, y497, x]);
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
	fracRange[0,10,l1] := fracRange[0,10,l1] / 2.0;
	 call ConstructLink(5,l2,l3);
packedRange[0,10,l3] := true;
fracRange[0,10,l3] := 1.0;
	fracRange[0,10,l2] := fracRange[0,10,l2] / 2.0;
	 call addModulo11(20,l3);
	fracRange[0,10,l3] := fracRange[0,10,l3] / 2.0;
	fracRange[0,10,l3] := fracRange[0,10,l3] * 2.0;
}
 