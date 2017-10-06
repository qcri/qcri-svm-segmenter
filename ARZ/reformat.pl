while (<STDIN>)
{
    chomp;
    if (/\-\-\-\-/)
    {
	print "\n";
    }
    else
    {
	print; print " ";
    }
}
