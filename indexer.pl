#!/usr/bin/perl -w
# indexer.pl produces an index to be used with buscaROM.
#
# Copyright 2003, Ramiro Gómez.
#
# This program is free software; you can redistribute it and/or
# modify it under the same terms as Perl itself.
use HTML::Parser 3.0;
use Carp;
use strict;
use Cwd; # module for finding the current working directory

#################################################################
# Configuration variables

# Path prefix
# You can set a prefix to the $path, e.g.,
# my $path_prefix = 'http://www.ramiro.org/';
# This will result in a fully qualified URL
# as the first entry for each indexed file.
my $path_prefix = 'http://www.ramiro.org/';

# Remove digits
# If set to a true value any digits within
# the text will be removed
my $rm_digits = 1;
#################################################################

# Globals
my %index; # where the index is stored before saving it to a file
my $index_file = 'index.dat'; # file where index is saved
my $file_count = 0; # How many files are processed

# Start of script
my $abs_path = shift or die "USAGE: $0 /path/to/dir\n";
scan_tree($abs_path);

# Write index to 'index.dat'
open(OUT, ">$index_file") or die "Can't open $index_file:$!";
# encode output in latin2
binmode(OUT, ":encoding(latin2)");
for my $key (sort keys %index) {
    print OUT "$key|", "$index{$key}{title}|", join(" ", @{$index{$key}{text}}), "\n";
}
close(OUT) or die "Can't close $index_file:$!";
# Print report
print $file_count, " HTML files indexed.\nIndex was written to 'index.dat' in your current working directory.\n";

####################### Subroutines #############################
sub scan_tree {
    my $workdir = shift; 
    my $startdir = cwd(); # keep track of where we began

    chdir($workdir) or die "Unable to enter $workdir:$!\n";

    opendir(DIR, ".") or die "Unable to open $workdir:$!\n";
    my @files = readdir(DIR);
    closedir(DIR);
 
    foreach my $file (@files){
        next if ($file eq ".") || ($file eq ".."); 
	
        if (-d $file){ # is this a directory
            scan_tree($file);
        } elsif (-f $file && $file =~ /\.html?$/) {
	    my $path = cwd . '/' . $file;
	    $file_count++;
	    parse_doc($file,$path);
	}
    }
    chdir($startdir) or die "Unable to change to dir $startdir:$!\n";
}

# Parse HTML file and extract original text with general entities decoded
sub parse_doc {
    my $file = shift;
    my $path = shift;
    my ($text,$title,$content);

    # Original text will be stored in $text and the title in $title
    my $p = HTML::Parser->new(api_version => 3,
			      start_h => [ sub { my ($self, $tag) = @_;
						 if ($tag eq 'title') {
						     $self->{lasttag} = 'title';
						 }
					     }, 'self,tagname' ],
			      text_h => [ sub { my $self = $_[0];
						$text .= $_[1];
						if (defined($self->{lasttag}) && $self->{lasttag} eq 'title') {
						    $title = $_[1];
						    undef $self->{lasttag};
						}
					    }, "self,dtext" ],
			      comment_h => [""],
			      ignore_elements => [ qw(script style) ]
			      );
    $p->parse_file($file);
    $p->eof;
    
    # 'clean' title string and substitute with dummy title if empty
    $title =~ s/^\s*|\s*$//g; # remove leading and trailing whitespace
    $title =~ s/\|//g; # remove vertical bars
    $title =~ s/\n/ /g; # replace newlines
    if (!$title) { $title = 'DOCUMENT TITLE'; }
    
    # Get a list of unique keywords (set locale for languages like
    # German, French and Spanish to the corresponding values)
    my @keywords = extract_keywords(text => $text, locale => 'de_DE');
    
    # If a $path_prefix was set prepend it to $path
    if ($path_prefix) { $path = $path_prefix . $path; }

    # Remove the absolute path from the $path variable
    # so that relative paths remain
    $path =~ s/$abs_path//;

    $index{$path} = { title => $title, text => \@keywords };
}

# Extract a hash of keywords, no dublettes remain and no phrase search
# possible after calling this subroutine
sub extract_keywords {
    my %args = @_;
    my $text = lc $args{text}; # lowercase all text
    my $locale = $args{locale};
    my %keywords;

    # Remove any digits, if $rm_digits is true 
    if ($rm_digits) { $text =~ s/\d//g; }
    
    # Set local to the specified language to influence
    # the behaviour of the Perl RegEx-engine
    if ($locale) {
	use locale;
	use POSIX 'locale_h';
	setlocale(LC_CTYPE, $locale) or croak "Invalid locale $locale";
	while ($text =~ m/(\w+)/g) {
	    $keywords{$1}++;
	} 
    } else {
	while ($text =~ m/(\w+)/g) {
	    $keywords{$1}++;
	}
    }
    return sort (keys %keywords);
}
__END__
TODO
remove stopwords from the keywords array
