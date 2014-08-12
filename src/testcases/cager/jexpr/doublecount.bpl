type Ref;
type PredicateTypes;
type FractionType = [Ref, PredicateTypes] int;
type PackedType = [Ref, PredicateTypes] bool;
var packed: PackedType;
var frac: FractionType;
const null: Ref;

var val: [Ref]int;
var dbl: [Ref]int;
const unique OKP: PredicateTypes;

procedure PackOK(this:Ref);
requires (dbl[this]==2*val[this]);

procedure UnpackOK(this:Ref);
requires packed[this, OKP];
ensures (dbl[this]==2*val[this]);

procedure increment(this:Ref)
modifies val, dbl, packed, frac;
requires packed[this,okP] && (frac[this,okP] > 0);
ensures packed[this,okP] && (frac[this,okP] > 0);
{
call UnpackOK(this);
packed[this, OKP]:=false;
val[this]:=val[this]+1;
dbl[this]:=dbl[this]+2;
call PackOK(this);
packed[this, OKP]:=true;
}
 