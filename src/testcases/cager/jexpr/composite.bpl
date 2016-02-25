type Ref;
const null: Ref;

var count: [Ref]int;
var left: [Ref]Ref;
var right: [Ref]Ref;
var parent: [Ref]Ref;
var packedCount: [Ref] bool;
var fracCount: [Ref] real;
var packedLeft: [Ref] bool;
var fracLeft: [Ref] real;
var packedParent: [Ref] bool;
var fracParent: [Ref] real;
var packedRight: [Ref] bool;
var fracRight: [Ref] real;

procedure ConstructComposite(count1 :int, left1 : Ref, right1 : Ref, parent1 : Ref, this: Ref);
	 ensures (count[this] == count1) &&
 	 	 (left[this] == left1) &&
 	 	 (right[this] == right1) &&
 	 	 (parent[this] == parent1); 
 
procedure PackCount(c:int, ol: Ref, or: Ref, lc:int, rc:int, this:Ref);
	 requires (packedCount[this]==false) &&
	 	((((count[this]==((lc+rc)+1)))&&(fracLeft[this] >= 0.5 && (left[this]==ol) && (count[left[this]]==lc)))&&(fracRight[this] >= 0.5 && (right[this]==or) && (count[right[this]]==rc))) && (count[this]==c); 
 
procedure UnpackCount(c:int, ol: Ref, or: Ref, lc:int, rc:int, this:Ref);
	 requires packedCount[this] &&
	 	 (fracCount[this] > 0.0);
	 ensures ((((count[this]==((lc+rc)+1)))&&(fracLeft[this] >= 0.5 && (left[this]==ol) && (count[left[this]]==lc)))&&(fracRight[this] >= 0.5 && (right[this]==or) && (count[right[this]]==rc))) && (count[this]==c);

procedure PackParent(op: Ref, c:int, this:Ref);
	 requires (packedParent[this]==false) &&
	 	(((((((parent[this]!=this))&&(fracCount[this] >= 0.5 && (count[this]==c)))&&((parent[this]!=null)==>(fracParent[parent[this]] > 0.0)))&&(((parent[this]!=null)&&(left[op]==this))==>(fracLeft[parent[this]] >= 0.5 && (left[parent[this]]==this) && (count[left[parent[this]]]==c))))&&(((parent[this]!=null)&&(right[op]==this))==>(fracRight[parent[this]] >= 0.5 && (right[parent[this]]==this) && (count[right[parent[this]]]==c))))&&((parent[this]==null)==>(fracCount[this] >= 0.5 && (count[this]==c)))) && (parent[this]==op); 
 
procedure UnpackParent(op: Ref, c:int, this:Ref);
	 requires packedParent[this] &&
	 	 (fracParent[this] > 0.0);
	 ensures (((((((parent[this]!=this))&&(fracCount[this] >= 0.5 && (count[this]==c)))&&((parent[this]!=null)==>(fracParent[parent[this]] > 0.0)))&&(((parent[this]!=null)&&(left[op]==this))==>(fracLeft[parent[this]] >= 0.5 && (left[parent[this]]==this) && (count[left[parent[this]]]==c))))&&(((parent[this]!=null)&&(right[op]==this))==>(fracRight[parent[this]] >= 0.5 && (right[parent[this]]==this) && (count[right[parent[this]]]==c))))&&((parent[this]==null)==>(fracCount[this] >= 0.5 && (count[this]==c)))) && (parent[this]==op);

procedure PackLeft(ol: Ref, lc:int, op: Ref, this:Ref);
	 requires (packedLeft[this]==false) &&
	 	((((left[this]==null)==>(lc==0)))&&((left[this]!=null)==>(((fracCount[left[this]] >= 0.5 && (count[left[this]]==lc))&&(left[this]!=this))&&(left[this]!=parent[this])))) && (left[this]==ol) && (parent[this]==op); 
 
procedure UnpackLeft(ol: Ref, lc:int, op: Ref, this:Ref);
	 requires packedLeft[this] &&
	 	 (fracLeft[this] > 0.0);
	 ensures ((((left[this]==null)==>(lc==0)))&&((left[this]!=null)==>(((fracCount[left[this]] >= 0.5 && (count[left[this]]==lc))&&(left[this]!=this))&&(left[this]!=parent[this])))) && (left[this]==ol) && (parent[this]==op);

procedure PackRight(or: Ref, rc:int, op: Ref, this:Ref);
	 requires (packedRight[this]==false) &&
	 	((((right[this]==null)==>(rc==0)))&&((right[this]!=null)==>(((fracCount[right[this]] >= 0.5 && (count[right[this]]==rc))&&(right[this]!=this))&&(right[this]!=parent[this])))) && (right[this]==or) && (parent[this]==op); 
 
procedure UnpackRight(or: Ref, rc:int, op: Ref, this:Ref);
	 requires packedRight[this] &&
	 	 (fracRight[this] > 0.0);
	 ensures ((((right[this]==null)==>(rc==0)))&&((right[this]!=null)==>(((fracCount[right[this]] >= 0.5 && (count[right[this]]==rc))&&(right[this]!=this))&&(right[this]!=parent[this])))) && (right[this]==or) && (parent[this]==op);

procedure updateCount(c:int,c1:int,c2:int,c3:int,ol:Composite,or:Composite,op:Composite,this:Ref)
	 modifies count,fracCount,fracLeft,fracRight,packedCount,packedLeft,packedRight;
	 requires (this != null) && ((((((parent[this]==op)&&(packedCount[this]== false) && 
 	 	(fracCount[this] >= 1.0) && (count[this]==c))&&(packedLeft[this]) && 
 	 	(fracLeft[this] >= 0.5) && (left[this]==ol) && (count[left[this]]==c1))&&(packedRight[this]) && 
 	 	(fracRight[this] >= 0.5) && (right[this]==or) && (count[right[this]]==c2))&&((op!=null)==>(packedCount[parent[this]]== false) && 
 	 	(fracCount[parent[this]] > 0.0) && (count[op]==c3)))&&(((packedLeft[parent[this]]== false) && 
 	 	(fracLeft[parent[this]] > 0.0) && (left[op]==left[op]) && (count[left[op]]==c)||(packedRight[parent[this]]== false) && 
 	 	(fracRight[parent[this]] > 0.0) && (right[op]==right[op]) && (count[right[op]]==c))||(op==null)));
	 ensures (packedCount[this]) && 
 	 	(fracCount[this] >= 1.0) && (count[this]==((c1+c2)+1));
	 requires (forall x:Ref :: ((y!=this) && (y!=op) ==>  packedCount[x]));
	 requires (forall x:Ref :: ((y!=op) ==>  packedLeft[x]));
	 requires (forall x:Ref :: ((y!=op) ==>  packedRight[x]));
	 ensures (forall x:Ref:: (packedCount[x] == old(packedCount[x])));
	 ensures (forall x:Ref:: (fracCount[x] == old(fracCount[x])));

	 ensures (forall x:Ref:: (packedLeft[x] == old(packedLeft[x])));
	 ensures (forall x:Ref:: (fracLeft[x] == old(fracLeft[x])));

	 ensures (forall x:Ref:: (packedRight[x] == old(packedRight[x])));
	 ensures (forall x:Ref:: (fracRight[x] == old(fracRight[x])));

{
	 var ol1:Ref;
	 var or1:Ref;
	 var lc1:int;
	 var rc1:int;
	 var ol2:Ref;
	 var or2:Ref;
	 var lc2:int;
	 var rc2:int;
	 var newc:int;
newc:=1;
call UnpackLeft(ol, c1, op, this);
if ([ol]!=null) {
 fracCount[ol] := fracCount[ol]+0.5;
}
packedLeft[this] := false;
if (left[this]!=null)
{
call UnpackCount(c1, ol1, or1, lc1, rc1, ol);
if ([or]!=null) {
 fracLeft[this] := fracLeft[this]+0.5;
}
if ([or]!=null) {
 fracRight[this] := fracRight[this]+0.5;
}
packedCount[ol] := false;
newc:=newc+count[left[this]];
call PackCount(c1, ol1, or1, lc1, rc1, ol);
if ([or]!=null) {
 fracLeft[this] := fracLeft[this]-0.5;
}
if ([or]!=null) {
 fracRight[this] := fracRight[this]-0.5;
}
packedCount[ol] := true;
}
 call PackLeft(ol, c1, op, this);
if ([ol]!=null) {
 fracCount[ol] := fracCount[ol]-0.5;
}
packedLeft[this] := true;
call UnpackRight(or, c2, op, this);
if ([or]!=null) {
 fracCount[or] := fracCount[or]+0.5;
}
packedRight[this] := false;
if (right[this]!=null)
{
call UnpackCount(c2, ol2, or2, lc2, rc2, or);
if ([or]!=null) {
 fracLeft[this] := fracLeft[this]+0.5;
}
if ([or]!=null) {
 fracRight[this] := fracRight[this]+0.5;
}
packedCount[or] := false;
newc:=newc+count[right[this]];
call PackCount(c2, ol2, or2, lc2, rc2, or);
if ([or]!=null) {
 fracLeft[this] := fracLeft[this]-0.5;
}
if ([or]!=null) {
 fracRight[this] := fracRight[this]-0.5;
}
packedCount[or] := true;
}
 call PackRight(or, c2, op, this);
if ([or]!=null) {
 fracCount[or] := fracCount[or]-0.5;
}
packedRight[this] := true;
count[this]:=newc;
call PackCount(newc, ol, or, c1, c2, this);
if ([or]!=null) {
 fracLeft[this] := fracLeft[this]-0.5;
}
if ([or]!=null) {
 fracRight[this] := fracRight[this]-0.5;
}
packedCount[this] := true;
}
 procedure updateCountRec(opp:Composite,lcc:int,or:Composite,ol:Composite,lc:int,rc:int,this:Ref)
	 modifies count,fracCount,fracLeft,fracParent,fracRight,packedCount,packedLeft,packedParent,packedRight;
	 requires (this != null) && ((((((packedParent[this] == false) && 
 	 	(fracParent[this] > 0.0)&&(parent[this]==opp))&&(opp!=this))&&((opp!=null)==>(packedParent[parent[this]]) && 
 	 	(fracParent[parent[this]] > 0.0)))&&((opp!=null)&&(left[opp]==this)))==>(((packedLeft[parent[this]]) && 
 	 	(fracLeft[parent[this]] >= 0.5) && (left[opp]==this) && (count[left[opp]]==lcc)&&((opp!=null)&&(right[opp]==this)))==>(((((packedRight[parent[this]]) && 
 	 	(fracRight[parent[this]] >= 0.5) && (right[opp]==this) && (count[right[opp]]==lcc)&&((opp==null)==>(packedCount[this]) && 
 	 	(fracCount[this] >= 0.5) && (count[this]==lcc)))&&(packedCount[this]== false) && 
 	 	(fracCount[this] >= 0.5) && (count[this]==lcc))&&(packedLeft[this]) && 
 	 	(fracLeft[this] >= 0.5) && (left[this]==ol) && (count[left[this]]==lc))&&(packedRight[this]) && 
 	 	(fracRight[this] >= 0.5) && (right[this]==or) && (count[right[this]]==rc))));
	 ensures (packedParent[this] ) && 
 	 	(fracParent[this] > 0.0);
	 requires (forall x:Ref :: ((y!=this) ==>  packedCount[x]));
	 requires (forall x:Ref :: packedLeft[x]);
	 requires (forall x:Ref :: ((y!=this) ==>  packedParent[x]));
	 requires (forall x:Ref :: packedRight[x]);
	 ensures (forall x:Ref:: (packedCount[x] == old(packedCount[x])));
	 ensures (forall x:Ref:: (fracCount[x] == old(fracCount[x])));

	 ensures (forall x:Ref:: (packedParent[x] == old(packedParent[x])));
	 ensures (forall x:Ref:: (fracParent[x] == old(fracParent[x])));

	 ensures (forall x:Ref:: (packedLeft[x] == old(packedLeft[x])));
	 ensures (forall x:Ref:: (fracLeft[x] == old(fracLeft[x])));

	 ensures (forall x:Ref:: (packedRight[x] == old(packedRight[x])));
	 ensures (forall x:Ref:: (fracRight[x] == old(fracRight[x])));

{
	 var oll:Ref;
	 var orr:Ref;
	 var llc:int;
	 var rrc:int;
if (parent[this]!=null)
{
fracParent[opp] := fracParent[opp];
call UnpackParent(parent[opp], count[opp], opp);
if ([or]!=null) {
 fracCount[this] := fracCount[this]+0.5;
}
if ([op]!=null) {
 fracParent[op] := fracParent[op]+k;
}
if ([op]!=null&&[op]left[op]==this) {
 fracLeft[op] := fracLeft[op]+0.5;
}
if ([op]!=null&&[op]right[op]==this) {
 fracRight[op] := fracRight[op]+0.5;
}
if ([op]==null) {
 fracCount[this] := fracCount[this]+0.5;
}
packedParent[opp] := false;
call UnpackCount(, oll, this, llc, lcc, opp);
if ([or]!=null) {
 fracLeft[this] := fracLeft[this]+0.5;
}
if ([or]!=null) {
 fracRight[this] := fracRight[this]+0.5;
}
packedCount[opp] := false;
if (this==right[parent[this]])
{
fracRight[opp] := fracRight[opp]+0.5;
call UnpackRight(this, lcc, parent[opp], opp);
if ([or]!=null) {
 fracCount[or] := fracCount[or]+0.5;
}
packedRight[opp] := false;
fracCount[this] := fracCount[this]+0.5;
	 call updateCount(lcc, ol, or, lc, rc, count[opp], this);
if ([op]==null) {
 fracCount[this] := fracCount[this]-1.0;
}
if ([op]==null) {
 fracLeft[this] := fracLeft[this]-0.5;
}
if ([op]==null) {
 fracRight[this] := fracRight[this]-0.5;
}
if ([op]!=null) {
 fracCount[op] := fracCount[op]-k;
}
if ([op]!=null) {
 fracLeft[op] := fracLeft[op]-k;
}
if ([op]!=null) {
 fracRight[op] := fracRight[op]-k;
}
if ([op]!=null) {
 fracCount[this] := fracCount[this]+1.0;
;
call PackParent(opp, lc+rc+1, this);
if ([or]!=null) {
 fracCount[this] := fracCount[this]-0.5;
}
if ([op]!=null) {
 fracParent[op] := fracParent[op]-k;
}
if ([op]!=null&&[op]left[op]==this) {
 fracLeft[op] := fracLeft[op]-0.5;
}
if ([op]!=null&&[op]right[op]==this) {
 fracRight[op] := fracRight[op]-0.5;
}
if ([op]==null) {
 fracCount[this] := fracCount[this]-0.5;
}
packedParent[this] := true;
call PackRight(this, lcc, parent[opp], opp);
if ([or]!=null) {
 fracCount[or] := fracCount[or]-0.5;
}
packedRight[opp] := true;
	 call updateCountRec(parent[opp], count[opp], left[opp], this, count[left[opp]], lc+rc+1,this);
if (this[k1][parent]) {
 fracParent[this] := fracParent[this]-k1;
}
if ([opp]!=null) {
 fracParent[opp] := fracParent[opp]-k;
}
if ([opp]0.5[left]this[lcc]) {
 fracLeft[opp] := fracLeft[opp]-0.5;
}
if ([opp]0.5[left]this[lcc]&&[opp]!=null&&[opp]right[opp]==this) {
 fracRight[opp] := fracRight[opp]-0.5;
}
if ([opp]==null) {
 fracCount[this] := fracCount[this]-0.5;
}
if ([opp]==null) {
 fracCount[this] := fracCount[this]-0.5;
}
if ([opp]==null) {
 fracLeft[this] := fracLeft[this]-0.5;
}
if ([opp]==null) {
 fracRight[this] := fracRight[this]-0.5;
}
if ([opp]==null) {
 fracParent[this] := fracParent[this]+k2;
;
}
 else 
if (this==left[parent[this]])
{
fracLeft[opp] := fracLeft[opp]+0.5;
call UnpackLeft(this, lcc, parent[opp], opp);
if ([ol]!=null) {
 fracCount[ol] := fracCount[ol]+0.5;
}
packedLeft[opp] := false;
fracCount[this] := fracCount[this]+0.5;
	 call updateCount(lcc, ol, or, lc, rc, count[opp], this);
if ([op]==null) {
 fracCount[this] := fracCount[this]-1.0;
}
if ([op]==null) {
 fracLeft[this] := fracLeft[this]-0.5;
}
if ([op]==null) {
 fracRight[this] := fracRight[this]-0.5;
}
if ([op]!=null) {
 fracCount[op] := fracCount[op]-k;
}
if ([op]!=null) {
 fracLeft[op] := fracLeft[op]-k;
}
if ([op]!=null) {
 fracRight[op] := fracRight[op]-k;
}
if ([op]!=null) {
 fracCount[this] := fracCount[this]+1.0;
;
call PackParent(opp, lc+rc+1, this);
if ([or]!=null) {
 fracCount[this] := fracCount[this]-0.5;
}
if ([op]!=null) {
 fracParent[op] := fracParent[op]-k;
}
if ([op]!=null&&[op]left[op]==this) {
 fracLeft[op] := fracLeft[op]-0.5;
}
if ([op]!=null&&[op]right[op]==this) {
 fracRight[op] := fracRight[op]-0.5;
}
if ([op]==null) {
 fracCount[this] := fracCount[this]-0.5;
}
packedParent[this] := true;
call PackLeft(this, lcc, parent[opp], opp);
if ([ol]!=null) {
 fracCount[ol] := fracCount[ol]-0.5;
}
packedLeft[opp] := true;
	 call updateCountRec(parent[opp], count[opp], this, right[opp], count[left[opp]], lc+rc+1,this);
if (this[k1][parent]) {
 fracParent[this] := fracParent[this]-k1;
}
if ([opp]!=null) {
 fracParent[opp] := fracParent[opp]-k;
}
if ([opp]0.5[left]this[lcc]) {
 fracLeft[opp] := fracLeft[opp]-0.5;
}
if ([opp]0.5[left]this[lcc]&&[opp]!=null&&[opp]right[opp]==this) {
 fracRight[opp] := fracRight[opp]-0.5;
}
if ([opp]==null) {
 fracCount[this] := fracCount[this]-0.5;
}
if ([opp]==null) {
 fracCount[this] := fracCount[this]-0.5;
}
if ([opp]==null) {
 fracLeft[this] := fracLeft[this]-0.5;
}
if ([opp]==null) {
 fracRight[this] := fracRight[this]-0.5;
}
if ([opp]==null) {
 fracParent[this] := fracParent[this]+k2;
;
}
 }
 else 
fracCount[this] := fracCount[this]+0.5;
	 call updateCount(lcc, ol, or, lc, rc, count[opp], this);
if ([op]==null) {
 fracCount[this] := fracCount[this]-1.0;
}
if ([op]==null) {
 fracLeft[this] := fracLeft[this]-0.5;
}
if ([op]==null) {
 fracRight[this] := fracRight[this]-0.5;
}
if ([op]!=null) {
 fracCount[op] := fracCount[op]-k;
}
if ([op]!=null) {
 fracLeft[op] := fracLeft[op]-k;
}
if ([op]!=null) {
 fracRight[op] := fracRight[op]-k;
}
if ([op]!=null) {
 fracCount[this] := fracCount[this]+1.0;
;
fracCount[this] := fracCount[this];
call PackParent(parent[this], lc+rc+1, this);
if ([or]!=null) {
 fracCount[this] := fracCount[this]-0.5;
}
if ([op]!=null) {
 fracParent[op] := fracParent[op]-k;
}
if ([op]!=null&&[op]left[op]==this) {
 fracLeft[op] := fracLeft[op]-0.5;
}
if ([op]!=null&&[op]right[op]==this) {
 fracRight[op] := fracRight[op]-0.5;
}
if ([op]==null) {
 fracCount[this] := fracCount[this]-0.5;
}
packedParent[this] := true;
}
 }
 procedure setLeft(l:Composite,this:Ref)
	 modifies count,fracCount,fracLeft,fracParent,fracRight,left,packedCount,packedLeft,packedParent,packedRight,parent;
	 requires (this != null) && (((((((this!=l)&&(l!=null))&&(right[this]!=parent[this]))&&(l!=parent[this]))&&(this!=right[this]))&&(packedParent[this] ) && 
 	 	(fracParent[this] > 0.0))&&(packedParent[l] == false) && 
 	 	(fracParent[l] > 0.0));
	 ensures (packedParent[this] ) && 
 	 	(fracParent[this] > 0.0);
	 requires (forall x:Ref :: packedCount[x]);
	 requires (forall x:Ref :: packedLeft[x]);
	 requires (forall x:Ref :: ((y!=l) ==>  packedParent[x]));
	 requires (forall x:Ref :: packedRight[x]);
	 ensures (forall x:Ref:: (packedCount[x] == old(packedCount[x])));
	 ensures (forall x:Ref:: (fracCount[x] == old(fracCount[x])));

	 ensures (forall x:Ref:: (packedParent[x] == old(packedParent[x])));
	 ensures (forall x:Ref:: (fracParent[x] == old(fracParent[x])));

	 ensures (forall x:Ref:: (packedLeft[x] == old(packedLeft[x])));
	 ensures (forall x:Ref:: (fracLeft[x] == old(fracLeft[x])));

	 ensures (forall x:Ref:: (packedRight[x] == old(packedRight[x])));
	 ensures (forall x:Ref:: (fracRight[x] == old(fracRight[x])));

{
	 var lcc:int;
	 var or:Ref;
if (parent[l]==null)
{
parent[l]:=this;
call UnpackParent(parent[this], lcc, this);
if ([or]!=null) {
 fracCount[this] := fracCount[this]+0.5;
}
if ([op]!=null) {
 fracParent[op] := fracParent[op]+k;
}
if ([op]!=null&&[op]left[op]==this) {
 fracLeft[op] := fracLeft[op]+0.5;
}
if ([op]!=null&&[op]right[op]==this) {
 fracRight[op] := fracRight[op]+0.5;
}
if ([op]==null) {
 fracCount[this] := fracCount[this]+0.5;
}
packedParent[this] := false;
call UnpackCount(lcc, null, or, 0, count[right[this]], this);
if ([or]!=null) {
 fracLeft[this] := fracLeft[this]+0.5;
}
if ([or]!=null) {
 fracRight[this] := fracRight[this]+0.5;
}
packedCount[this] := false;
fracLeft[this] := fracLeft[this]+0.5;
call UnpackLeft(null, 0, parent[this], this);
if ([ol]!=null) {
 fracCount[ol] := fracCount[ol]+0.5;
}
packedLeft[this] := false;
left[this]:=l;
count[left[this]]:=count[left[l]];
call PackLeft(l, , parent[this], this);
if ([ol]!=null) {
 fracCount[ol] := fracCount[ol]-0.5;
}
packedLeft[this] := true;
fracLeft[this] := fracLeft[this];
call PackParent(parent[l], count[left[l]], l);
if ([or]!=null) {
 fracCount[this] := fracCount[this]-0.5;
}
if ([op]!=null) {
 fracParent[op] := fracParent[op]-k;
}
if ([op]!=null&&[op]left[op]==this) {
 fracLeft[op] := fracLeft[op]-0.5;
}
if ([op]!=null&&[op]right[op]==this) {
 fracRight[op] := fracRight[op]-0.5;
}
if ([op]==null) {
 fracCount[this] := fracCount[this]-0.5;
}
packedParent[l] := true;
	 call updateCountRec(parent[this], lcc, l, right[this], count[left[l]], count[right[this]], this);
if (this[k1][parent]) {
 fracParent[this] := fracParent[this]-k1;
}
if ([opp]!=null) {
 fracParent[opp] := fracParent[opp]-k;
}
if ([opp]0.5[left]this[lcc]) {
 fracLeft[opp] := fracLeft[opp]-0.5;
}
if ([opp]0.5[left]this[lcc]&&[opp]!=null&&[opp]right[opp]==this) {
 fracRight[opp] := fracRight[opp]-0.5;
}
if ([opp]==null) {
 fracCount[this] := fracCount[this]-0.5;
}
if ([opp]==null) {
 fracCount[this] := fracCount[this]-0.5;
}
if ([opp]==null) {
 fracLeft[this] := fracLeft[this]-0.5;
}
if ([opp]==null) {
 fracRight[this] := fracRight[this]-0.5;
}
if ([opp]==null) {
 fracParent[this] := fracParent[this]+k2;
;
}
 }
 