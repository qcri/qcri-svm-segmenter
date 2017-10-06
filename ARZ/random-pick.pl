# split randomly

# open (TRAIN, ">train.lang.ar.sample") or die;
open (TEST, ">train.lang.ar.sample") or die;

srand();

$tot = 20780;
$tr = 305;

while (<STDIN>)
{
    $r = rand();
    $t = $tr/$tot;
    if ($r > $t)
    {
#        print TRAIN;
    }
    else
    {
        print TEST;
	$tr--;
    }
    $tot--;
}

close TEST;
close TRAIN;
