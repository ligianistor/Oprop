//----------------------------------
//new class

var dc: [Ref]Ref;
var packedShareCount: [Ref] bool;
var fracShareCount: [Ref] real;

procedure ConstructShare(dc1 : Ref, this: Ref);
	 ensures (dc[this] == dc1); 
 
procedure PackShareCount(d:DoubleCount, this:Ref);
	 requires packedShareCount[this]==false &&
	 	(fracOK[dc[this]] == 0.1); 
 
procedure UnpackShareCount(d:DoubleCount, this:Ref);
	 requires packedShareCount[this] &&
	 	 (fracShareCount[this] > 0.0);
	 ensures (fracOK[dc[this]] == 0.1);

procedure touch(this:Ref)
	 modifies dbl,dc,fracOK,packedOK,packedShareCount,val;
	 requires packedShareCount[this] && 
 	 	(fracShareCount[this] > 0.0);
	 ensures packedShareCount[this] && 
 	 	(fracShareCount[this] > 0.0);
	 requires (forall x:Ref :: packedShareCount[x]);
	 requires (forall x:Ref :: packedOK[x]);
	 ensures (forall x:Ref:: (packedOK[x] == old(packedOK[x])));
	 ensures (forall x:Ref:: (fracOK[x] == old(fracOK[x])));

	 ensures (forall x:Ref:: (packedShareCount[x] == old(packedShareCount[x])));

{
call UnpackShareCount(dc[this], this);
packedShareCount[this] := false;
	 call increment(this);
	fracOK[this] := fracOK[this] / 2.0;
	fracOK[this] := fracOK[this] * 2.0;
call PackShareCount(dc[this], this);
packedShareCount[this] := true;
}
 procedure main(this:Ref)
	 modifies dbl,dc,fracOK,fracShareCount,packedOK,packedShareCount,val;
	 requires (forall x:Ref :: packedShareCount[x]);
	 requires (forall x:Ref :: packedOK[x]);
{
	 var dc0:Ref;
	 var s1:Ref;
	 var s2:Ref;
	 call ConstructDoubleCount(2,4,dc0);
packedOK[dc0] := true;
fracOK[dc0] := 1.0;
	 call ConstructShare(dc0,s1);
packedShareCount[s1] := true;
fracShareCount[s1] := 1.0;
	fracOK[dc0] := fracOK[dc0] / 2.0;
	 call ConstructShare(dc0,s2);
packedShareCount[s2] := true;
fracShareCount[s2] := 1.0;
	fracOK[dc0] := fracOK[dc0] / 2.0;
	 call touch(s1);
	fracShareCount[s1] := fracShareCount[s1] / 2.0;
	fracShareCount[s1] := fracShareCount[s1] * 2.0;
	 call touch(s2);
	fracShareCount[s2] := fracShareCount[s2] / 2.0;
	fracShareCount[s2] := fracShareCount[s2] * 2.0;
}
 