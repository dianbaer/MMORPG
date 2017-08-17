package _45degrees.com.friendsofed.isometric
{
	

	public class Data
	{
		private var _attackSpeed:Number = 0;
		private var _moveV:Number = 0;
		private var _jumpVerticalV:Number = 0;
		private var _jumpVerticalA:Number = 0;
		private var _hp:int;
		private var _maxHp:int;
		private var _att:int;
		private var _def:int;
		public function Data()
		{
		}
		public function decHP(value:int):void{
			this.hp = this.hp-value;
			if(this.hp < 0){
				this.hp = 0;
			}
		}
		public function addHP(value:int):void{
			this.hp = this.hp+value;
			if(this.hp > maxHp){
				this.hp = maxHp;
			}
		}
		public function get def():int
		{
			return _def;
		}

		public function set def(value:int):void
		{
			_def = value;
		}

		public function get att():int
		{
			return _att;
		}

		public function set att(value:int):void
		{
			_att = value;
		}

		public function get maxHp():int
		{
			return _maxHp;
		}

		public function set maxHp(value:int):void
		{
			_maxHp = value;
		}

		public function get hp():int
		{
			return _hp;
		}

		public function set hp(value:int):void
		{
			_hp = value;
		}

		public function get jumpVerticalA():Number
		{
			return _jumpVerticalA;
		}

		public function set jumpVerticalA(value:Number):void
		{
			_jumpVerticalA = value;
		}

		

		public function get jumpVerticalV():Number
		{
			return _jumpVerticalV;
		}

		public function set jumpVerticalV(value:Number):void
		{
			_jumpVerticalV = value;
		}

		public function get moveV():Number
		{
			return _moveV;
		}

		public function set moveV(value:Number):void
		{
			_moveV = value;
		}

		public function get attackSpeed():Number
		{
			return _attackSpeed;
		}

		public function set attackSpeed(value:Number):void
		{
			_attackSpeed = value;
		}
		public function clone(data:Data = null):Data
		{
			if(data == null) data = new Data();
			data._attackSpeed = this._attackSpeed;
			data._moveV = this._moveV;
			data._jumpVerticalV = this._jumpVerticalV;
			data._jumpVerticalA = this._jumpVerticalA;
			data._hp = this._hp;
			data._maxHp = this._maxHp;
			data._att = this._att;
			data._def = this._def;
			return data;
		}
		

	}
}