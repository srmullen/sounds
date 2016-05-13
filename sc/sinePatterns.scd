(
s.boot;
s.meter;
s.plotTree;
s.scope;
)

(
SynthDef(\sine, { |out = 0, freq = 440, amp = 0.3, dur = 1, pan = 0|
	var sig, env;
	env = EnvGen.kr(Env.asr(releaseTime:0.01), gate: Line.kr(1, 0, dur), doneAction: 2);
	sig = SinOsc.ar(freq, rand(0.0, 2pi)) * amp * env;
	Out.ar(out, Pan2.ar(sig));
}).add;
)

( // #1
var chords, major, minor;
major = [0, 4, 7];
minor = [0, 3, 7];
chords = Pseq([major, minor, major] + [60, 63, 61], inf);
Pbind(
	\instrument, \sine,
	\midinote, chords,
	\dur, Pseq([3, 1.2], inf),
	\amp, Pseq([0.5, 0.2], inf)
).play;
)

( // #2 a child is born
var melody, durations, phrase1, phrase2, midi, codaDur;

phrase1 = Pseq([3/4, 1/4, 1/4, 1/4]);
phrase2 = Pseq([3/4, 1/2, 1/4]);
codaDur = Pseq([1/2, 1/8, 1/8, 1/2, 1/4, 1/8,  1/8, 1/2]);
durations = Pseq([
	Pseq([phrase1], 3),«
	phrase2,
	Pseq([phrase1], 4),
	Pseq([phrase1], 3),
	phrase2,
	Pseq([phrase1], 2),
	codaDur
], inf);
// wslib midi octaves seem to index from 0
midi = ([\d3, \eb3, \f3, \Bb3] ++
	[\d3, \eb3, \f3, \Bb3] ++
	[\d3, \eb3, \f3, \Bb3] ++
	[\d4, \c4, \eb3] ++
	[\d3, \eb3, \f3, \Bb3] ++
	[\d3, \eb3, \f3, \Bb3] ++
	[\d3, \e3, \g3, \c4] ++
	[\d4, \c4, \f3, \e3] ++
	[\d3, \eb3, \f3, \Bb3] ++
	[\d3, \eb3, \f3, \Bb3] ++
	[\d3, \eb3, \f3, \Bb3] ++
	[\f4, \eb4, \gb3] ++
	[\f3, \eb3, \f3, \Bb3] ++
	[\d3, \e3, \g3, \c4] ++
	// coda
	[\d4, \c0, \f3, \f3, \f3] ++
	[\c4, \Bb3]
).namemidi;
melody = Pseq(midi);

Pbind(
	\instrument, \sine,
	\midinote, melody,
	\dur, durations
).play;
)



